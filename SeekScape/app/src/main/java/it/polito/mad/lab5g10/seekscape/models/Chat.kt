package it.polito.mad.lab5g10.seekscape.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import it.polito.mad.lab5g10.seekscape.firebase.CommonModel
import it.polito.mad.lab5g10.seekscape.firebase.TheChatModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.plus


val chatHourFormat = DateTimeFormatter.ofPattern("HH:mm")


data class ChatMessage(
    var author: User,
    var date: LocalDateTime,
    var text: String,
): Serializable



class ChatMessageModel(id: String, travel:Travel?){
    private fun <T> createStateFlow(initialValue: T) = MutableStateFlow(initialValue)

    val travelId = id
    val travelValue: MutableStateFlow<Travel?> = createStateFlow(travel)

    val isChatLoadedValue = createStateFlow(false)
    val fetchingBefore: MutableStateFlow<LocalDateTime?> = createStateFlow(null)
    val fetchingAfter: MutableStateFlow<LocalDateTime?> = createStateFlow(null)
    val pauseFetchAfter = createStateFlow(true)
    val messagesValue = createStateFlow(listOf<ChatMessage>())

    fun updateTravel(value: Travel) { travelValue.tryEmit(value) }
    fun updateChatLoaded(value: Boolean) { isChatLoadedValue.tryEmit(value) }
    fun updatePauseAfterFetch(value: Boolean) { pauseFetchAfter.tryEmit(value) }

    fun updatefetchingAfter(value: LocalDateTime?) { fetchingAfter.tryEmit(value) }
    fun updatefetchingBefore(value: LocalDateTime?) { fetchingBefore.tryEmit(value) }


    fun addOldMessages(value: List<ChatMessage>) {
        val messageList = value + messagesValue.value
        messagesValue.tryEmit(messageList)
    }

    fun addNewMessages(value: List<ChatMessage>) {
        val messageList = messagesValue.value + value
        messagesValue.tryEmit(messageList)
    }

}

class ChatMessageViewModel(private val model: ChatMessageModel): ViewModel() {

    private val theChatModel = TheChatModel()

    val isChatLoaded = model.isChatLoadedValue
    val messages = model.messagesValue
    val travel = model.travelValue
    val pause = model.pauseFetchAfter

    init {
        viewModelScope.launch {
            if(model.travelValue.value==null){
                val travel = CommonModel.getTravelById(model.travelId, isTravelLite = true)
                if(travel!=null){
                    model.updateTravel(travel)
                }
            }
            val now = LocalDateTime.now()
            val messages = theChatModel.getMessages(model.travelId, before = now)
            model.addNewMessages(messages)
            model.updateChatLoaded(true)
            if(messages.isNotEmpty()){
                model.updatefetchingAfter(messages.last().date)
                val before = if(messages.size==20) messages.first().date else null
                model.updatefetchingBefore(before)
            } else {
                model.updatefetchingAfter(now)
            }
            model.updatePauseAfterFetch(false)
        }
        startPeriodicFetch()
    }

    fun loadPreviousMessages() {
        val before = model.fetchingBefore.value
        if (before==null) return
        viewModelScope.launch {
            model.updatePauseAfterFetch(true)
            val messages = theChatModel.getMessages(model.travelId, before = before)
            model.addOldMessages(messages)
            val beforeNew = if(messages.size==20) messages.first().date else null
            model.updatefetchingBefore(beforeNew)
            model.updatePauseAfterFetch(false)
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            model.updatePauseAfterFetch(true)
            val chatMessage = ChatMessage(
                AppState.myProfile.value,
                LocalDateTime.now(),
                text
            )
            theChatModel.addMessage(model.travelId, chatMessage)
            fetchNewMessages()
            model.updatePauseAfterFetch(false)
        }
    }


    private fun startPeriodicFetch() {
        viewModelScope.launch {
            while (isActive) {
                delay(3000)
                if(!model.pauseFetchAfter.value) {
                    fetchNewMessages()
                }
            }
        }
    }

    suspend fun fetchNewMessages() {
        if(model.fetchingAfter.value!=null){
            val newMessages = theChatModel.getMessages(
                travelId = model.travelId,
                after = model.fetchingAfter.value
            )
            if (newMessages.isNotEmpty()) {
                model.addNewMessages(newMessages)
                model.updatefetchingAfter(newMessages.last().date)
            }
        }
    }

}

class ChatMessageViewModelFactory(
    private val travelId: String,
    private val travel: Travel?
) : ViewModelProvider.Factory {
    private val model = ChatMessageModel(travelId, travel)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ChatMessageViewModel::class.java) ->
                ChatMessageViewModel(model) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}