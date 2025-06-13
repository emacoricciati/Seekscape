package it.polito.mad.lab5g10.seekscape.firebase

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.gson.Gson
import it.polito.mad.lab5g10.seekscape.firebase.toFirestoreModel
import it.polito.mad.lab5g10.seekscape.models.ChatMessage
import it.polito.mad.lab5g10.seekscape.models.NotificationItem
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime

class TheChatModel() {

    suspend fun addMessage(travelId: String, chatMessage: ChatMessage) {
        try {
            val docRef = Collections.travels.document(travelId)
            val travelFirebaseSnapshot = docRef.get().await()
            if(!travelFirebaseSnapshot.exists()) return

            var chatMessageFirebase = chatMessage.toFirestoreModel()
            docRef.update("travelChat",
                FieldValue.arrayUnion(chatMessageFirebase)
            ).await()
            Log.d("addMessage", "Message added to array successfully.")
        } catch (e: Exception) {
            Log.e("addMessage", "Failed to add message", e)
        }
    }

    suspend fun getMessages(travelId: String, after: LocalDateTime?=null, before: LocalDateTime?=null, pageSize: Int=20): List<ChatMessage> {
        try {
            val docRef = Collections.travels.document(travelId)
            val snapshot = docRef.get().await()

            val messagesRaw = snapshot.get("travelChat") as? List<Map<String, Any>> ?: emptyList()

            val messages = messagesRaw.mapNotNull { map ->
                try {
                    val json = Gson().toJson(map)
                    Gson().fromJson(json, ChatMessageFirestoreModel::class.java)
                } catch (e: Exception) {
                    Log.e("getMessages", "Error converting messages", e)
                    null
                }
            }

            var filteredMessage = listOf<ChatMessageFirestoreModel>()

            after?.let {
                val firstAfterIndex = messages.indexOfLast {
                    after.isBefore(LocalDateTime.parse(it.date, firebaseChatFormatter))
                }
                if (firstAfterIndex!=-1 && firstAfterIndex < messages.size) {
                    filteredMessage = messages.subList(firstAfterIndex, messages.size)
                } else {
                    filteredMessage= emptyList()
                }
            }

            before?.let {
                if(messages.size<=25){
                    filteredMessage=messages
                } else {

                    var low = 0
                    var high = messages.lastIndex
                    var lastBeforeIndex = -1

                    while (low <= high) {
                        val mid = (low + high) / 2
                        val date = LocalDateTime.parse(messages[mid].date, firebaseChatFormatter)
                        if (date.isBefore(before)) {
                            lastBeforeIndex = mid
                            low = mid + 1
                        } else {
                            high = mid - 1
                        }
                    }

                    if (lastBeforeIndex != -1) {
                        val start = if (lastBeforeIndex < pageSize+5) 0 else lastBeforeIndex - pageSize + 1
                        filteredMessage = messages.subList(start, lastBeforeIndex + 1)
                    } else {
                        filteredMessage = emptyList()
                    }
                }
            }

            Log.d("getMessages", "Messages fetched.")
            return filteredMessage.map { it.toAppModel() }
        } catch (e: Exception) {
            Log.e("getMessages", "Failed fetch messages", e)
            return listOf()
        }
    }

}
