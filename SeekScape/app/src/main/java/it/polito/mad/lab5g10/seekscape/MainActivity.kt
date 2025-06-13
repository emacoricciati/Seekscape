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
        /*
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )

         */
    }
}

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Log.d("MainActivity", "onCreate called")
        //logIntentDetails(intent, "onCreate")

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
                NotificationPermissionRequester()
                //SeekScapeApp(initialIntent = intent)
                Support()
            }
        }
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun logIntentDetails(intent: Intent?, tag: String) {
        if (intent == null) {
            Log.e("MainActivity", "$tag: Intent is NULL")
            return
        }
        Log.d("MainActivity", "$tag: Intent Action: ${intent.action}")
        Log.d("MainActivity", "$tag: Intent Data: ${intent.data}")
        Log.d("MainActivity", "$tag: Intent Flags: ${intent.flags}")

        val extras = intent.extras
        if (extras == null) {
            Log.e("MainActivity", "$tag: Intent Extras are NULL")
        } else {
            Log.d("MainActivity", "$tag: Intent Extras:")
            for (key in extras.keySet()) {
                Log.d("MainActivity", "  $key: ${extras.get(key)}")
            }
        }
    }

    private fun handleIntent(intent: Intent?){
        intent?.let {
            val notificationId = it.getStringExtra("id")
            val notificationType = it.getStringExtra("type")
            val notificationTitle = it.getStringExtra("title")
            val notificationDescription = it.getStringExtra("description")
            val notificationTab = it.getStringExtra("tab")
            val notificationRoute = it.getStringExtra("navRoute")

            if(notificationId != null && notificationType!= null && notificationTitle!= null &&
                notificationDescription!= null && notificationTab!= null && notificationRoute!= null){
                val notificationItem = NotificationItem(
                    id = notificationId,
                    type = notificationType,
                    title = notificationTitle,
                    description = notificationDescription,
                    tab = notificationTab,
                    navRoute = notificationRoute
                )
                navigateToNotificationAction(notificationItem)
            }
            else{
                Log.w("MainActivity", "Missing Intent or incomplete intent")
            }
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