package it.polito.mad.lab5g10.seekscape

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import it.polito.mad.lab5g10.seekscape.firebase.firebaseFormatter
import com.google.firebase.messaging.FirebaseMessaging
import it.polito.mad.lab5g10.seekscape.firebase.CommonModel
import it.polito.mad.lab5g10.seekscape.firebase.CommonModel.getUser
import it.polito.mad.lab5g10.seekscape.models.Travel
import it.polito.mad.lab5g10.seekscape.models.User
import it.polito.mad.lab5g10.seekscape.ui.navigation.SeekScapeNavController
import java.io.File
import java.io.FileOutputStream
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import android.util.Base64
import it.polito.mad.lab5g10.seekscape.ui.navigation.MainDestinations
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec


fun <T : java.io.Serializable> getSerializableExtraCustom(intent: Intent, key: String, clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        intent.getSerializableExtra(key, clazz)
    } else {
        @Suppress("DEPRECATION")
        intent.getSerializableExtra(key) as? T
    }
}

object EncryptionUtils {
    private const val AES_MODE = "AES/GCM/NoPadding"
    private const val IV_SIZE = 12
    private const val TAG_LENGTH = 128
    private const val KEY_ALIAS = "MyChatAESKey"

    fun generateAndStoreAESKey() {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        if (keyStore.containsAlias(KEY_ALIAS)) return

        val keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore")
        val keyGenSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        keyGenerator.init(keyGenSpec)
        keyGenerator.generateKey()
    }

    private fun getAESKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val entry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        return entry?.secretKey ?: throw IllegalStateException("Key not found in Keystore")
    }

    fun encrypt(text: String): String {
        val cipher = Cipher.getInstance(AES_MODE)

        cipher.init(Cipher.ENCRYPT_MODE, getAESKey())
        val iv = cipher.iv // Initializaion Vector
        val cipherText = cipher.doFinal(text.toByteArray(Charsets.UTF_8))

        // Combine IV and ciphertext
        val encrypted = iv + cipherText
        return Base64.encodeToString(encrypted, Base64.NO_WRAP)
    }


    fun decrypt(encryptedBase64: String): String {
        val encrypted = Base64.decode(encryptedBase64, Base64.NO_WRAP)
        val iv = encrypted.copyOfRange(0, IV_SIZE)
        val cipherText = encrypted.copyOfRange(IV_SIZE, encrypted.size)

        val cipher = Cipher.getInstance(AES_MODE)
        val spec = GCMParameterSpec(TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, getAESKey(), spec)

        val plainText = cipher.doFinal(cipherText)
        return String(plainText, Charsets.UTF_8)
    }
}



fun formatDateTravel(startDAte: LocalDate, endDate: LocalDate): String {
    if (startDAte.year != endDate.year){
        return dayMonthYearFormat(startDAte) +" - "+dayMonthYearFormat(endDate)
    }
    return dayMonthFormat(startDAte) +" - "+dayMonthFormat(endDate)
}

fun timeAgo(date: LocalDate): String {
    val now = LocalDate.now()
    val future = date.isAfter(now)
    val period = if (future) Period.between(now, date) else Period.between(date, now)

    val result = when {
        period.years > 0 -> "${period.years} year${if (period.years > 1) "s" else ""}"
        period.months > 0 -> "${period.months} month${if (period.months > 1) "s" else ""}"
        period.days > 0 -> "${period.days} day${if (period.days > 1) "s" else ""}"
        else -> if (future) "soon" else "just now"
    }

    return if (result == "soon" || result == "just now") result
    else if (future) "in $result" else "$result ago"
}

fun dayMonthFormat(date: LocalDate): String {
    val dayMonthFormatter = DateTimeFormatter.ofPattern("dd/MM", Locale.getDefault()) // Use DateTimeFormatter
    return date.format(dayMonthFormatter)
}

fun dayMonthYearFormat(date: LocalDate): String {
    val dayMonthYearFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()) // Use DateTimeFormatter
    return date.format(dayMonthYearFormatter)

}

fun daysBetween(startDate: LocalDate, endDate: LocalDate): Int {
    val difference = ChronoUnit.DAYS.between(startDate, endDate)
    return difference.toInt()
}

fun calculateAge(birthDateStr: String): Int {
    if(birthDateStr==""){
        return 0;
    }
    val  birthDate = LocalDate.parse(birthDateStr, firebaseFormatter)!!
    val today = LocalDate.now()
    return Period.between(birthDate, today).years
}



fun copyUriToInternalStorage(context: Context, uri: Uri, fileName: String): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getScreenTitle(string: String): String {
    val string = string.replace("_", " ")
    val index = string.indexOf("/")
    return if (index != -1) {
        val firstPart = string.substring(0, index)
        val secondPart = string.substring(index + 1)
        firstPart.replaceFirstChar { it.uppercase() } + " "
    } else {
        string.replaceFirstChar { it.uppercase() }
    }
}

fun cleanStack(navController: NavHostController, redirectPath:String){
    navController.navigate(redirectPath){
        popUpTo(navController.currentDestination?.route ?: "") {
            inclusive = true
        }
    }
}

// List of all prefixes for phone numbers in the world
val phonePrefixes = listOf(
    "+1", "+7", "+20", "+27", "+30", "+31", "+32", "+33", "+34", "+36", "+39",
    "+40", "+41", "+43", "+44", "+45", "+46", "+47", "+48", "+49", "+51", "+52",
    "+53", "+54", "+55", "+56", "+57", "+58", "+60", "+61", "+62", "+63", "+64",
    "+65", "+66", "+81", "+82", "+84", "+86", "+90", "+91", "+92", "+93", "+94",
    "+95", "+98", "+211", "+212", "+213", "+216", "+218", "+220", "+221", "+222",
    "+223", "+224", "+225", "+226", "+227", "+228", "+229", "+230", "+231", "+232",
    "+233", "+234", "+235", "+236", "+237", "+238", "+239", "+240", "+241", "+242",
    "+243", "+244", "+245", "+246", "+248", "+249", "+250", "+251", "+252", "+253",
    "+254", "+255", "+256", "+257", "+258", "+260", "+261", "+262", "+263", "+264",
    "+265", "+266", "+267", "+268", "+269", "+290", "+291", "+297", "+298", "+299",
    "+350", "+351", "+352", "+353", "+354", "+355", "+356", "+357", "+358", "+359",
    "+370", "+371", "+372", "+373", "+374", "+375", "+376", "+377", "+378", "+380",
    "+381", "+382", "+383", "+385", "+386", "+387", "+389", "+420", "+421", "+423",
    "+500", "+501", "+502", "+503", "+504", "+505", "+506", "+507", "+508", "+509",
    "+590", "+591", "+592", "+593", "+594", "+595", "+596", "+597", "+598", "+599",
    "+670", "+672", "+673", "+674", "+675", "+676", "+677", "+678", "+679", "+680",
    "+681", "+682", "+683", "+685", "+686", "+687", "+688", "+689", "+690", "+691",
    "+692", "+850", "+852", "+853", "+855", "+856", "+870", "+880", "+886", "+960",
    "+961", "+962", "+963", "+964", "+965", "+966", "+967", "+968", "+970", "+971",
    "+972", "+973", "+974", "+975", "+976", "+977", "+992", "+993", "+994", "+995",
    "+996", "+998"
)

fun getMainDestination(path: String): String? {
    val parts = path.split("/")
    if (parts.isEmpty()){
        return null
    }
    val screen = parts[0]
    return when (screen){
        "travel" -> MainDestinations.HOME_ROUTE
        else -> MainDestinations.HOME_ROUTE
    }
}