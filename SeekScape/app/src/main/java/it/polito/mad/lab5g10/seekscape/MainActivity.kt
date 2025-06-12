package it.polito.mad.lab5g10.seekscape

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import android.Manifest
import androidx.lifecycle.lifecycleScope
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.NotificationItem
import it.polito.mad.lab5g10.seekscape.ui._theme.SeekScapeTheme
import navigateToNotificationAction
import it.polito.mad.lab5g10.seekscape.services.AccountService
import kotlinx.coroutines.launch


class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val result = FirebaseApp.initializeApp(this)
        println(">>> Firebase initialized: ${result != null}")

        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )
    }
}

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val accountService = AccountService()
        AppState.initialize(applicationContext)
        lifecycleScope.launch {
            if (accountService.hasUser()) {
                val userProfile = accountService.getUserProfile()
                if (userProfile != null){
                    AppState.setUserAsLogged()
                    AppState.updateMyProfile(userProfile)
                }
                Log.d("SeekScapeApp", "User is logged in: ${userProfile?.userId}")
            } else {
                AppState.setUserAsUnlogged()
                Log.d("SeekScapeApp", "No user is logged in")
            }
            AppState.doneFirstFetch()
        }
        setContent {
            SeekScapeTheme {
                //NotificationPermissionRequester()
                SeekScapeApp(initialIntent = intent)
                //Support()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent){
        intent.let {
            val notificationItem = NotificationItem(
                id = it.getStringExtra("notification_Id")!!,
                type = it.getStringExtra("notification_type")!!,
                title = it.getStringExtra("notification_title")!!,
                description = it.getStringExtra("notification_description")!!,
                tab = it.getStringExtra("notification_tab")!!,
                navRoute = it.getStringExtra("notification_route")!!
            )

            navigateToNotificationAction(notificationItem)
        }
    }
}

@Composable
fun NotificationPermissionRequester() {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                Log.d("Permissions", "Notification permission granted")
            } else {
                Log.d("Permissions", "Notification permission denied")
            }
        }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}