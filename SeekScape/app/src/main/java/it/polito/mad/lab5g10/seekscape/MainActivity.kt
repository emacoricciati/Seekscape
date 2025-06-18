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
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks

import com.google.firebase.auth.auth
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import android.Manifest
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.compose.runtime.mutableStateOf
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.NotificationItem
import it.polito.mad.lab5g10.seekscape.ui._theme.SeekScapeTheme
import it.polito.mad.lab5g10.seekscape.ui.navigation.navigateToNotificationAction
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



    private val dynamicLinkRouteState = mutableStateOf<String?>(null)
    private fun getDynamicLinks(intent: Intent){
        Log.d("MainActivity", "getDynamicLinks called with intent: $intent")
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener { pendingDynamicLinkData ->
                pendingDynamicLinkData?.link?.let { uri ->
                    val path = uri.path
                    val id = uri.getQueryParameter("id")
                    val email = uri.getQueryParameter("email")
                    val uid = uri.getQueryParameter("uid")

                    Log.d("MainActivity", "email: $email")

                    Log.d("MainActivity", "Dynamic link URI: $uri")

                    val route = when (path) {
                        "/travel" -> "travel/$id"
                        "/profile/reset_email_completed" -> "profile/reset_email_completed?uid=$uid&email=$email"
                        else -> null
                    }
                    dynamicLinkRouteState.value = route
                    Log.d("MainActivity", "Dynamic link route set to: $route")
                // Handle the dynamic link here
                } ?: run {
                    Log.d("MainActivity", "No dynamic link found")
                }
            }
            .addOnFailureListener { e ->
                Log.w("MainActivity", "getDynamicLink:onFailure", e)
            }
    }
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        if (BuildConfig.DEBUG) {
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }

        auth = Firebase.auth

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Log.d("MainActivity", "onCreate called")
        //logIntentDetails(intent, "onCreate")

        val accountService = AccountService()
        AppState.initialize(applicationContext)
        getDynamicLinks(intent)

        EncryptionUtils.generateAndStoreAESKey()

        logIntentDetails(intent, "onCreate")
        handleIntent(intent)

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
                SeekScapeApp(initialIntent = intent, dynamicRoute = dynamicLinkRouteState.value)
                //Support()
            }
        }
        hideNavigationBar()
//        handleIntent(intent)
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

    private fun hideNavigationBar() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
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