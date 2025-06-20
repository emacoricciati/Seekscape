package it.polito.mad.lab5g10.seekscape

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.navigation.NavHostController
import it.polito.mad.lab5g10.seekscape.firebase.firebaseFormatter
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import android.util.Base64
import it.polito.mad.lab5g10.seekscape.ui.navigation.MainDestinations
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec


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