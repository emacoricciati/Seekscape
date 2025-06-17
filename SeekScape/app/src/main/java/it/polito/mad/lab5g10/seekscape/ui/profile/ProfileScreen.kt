package it.polito.mad.lab5g10.seekscape.ui.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import androidx.compose.foundation.horizontalScroll
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.os.Build
import androidx.compose.ui.unit.dp
import it.polito.mad.lab5g10.seekscape.ui._common.components.PillButton
import it.polito.mad.lab5g10.seekscape.ui._common.components.UserImage
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import it.polito.mad.lab5g10.seekscape.copyUriToInternalStorage
import it.polito.mad.lab5g10.seekscape.firebase.CommonModel
import it.polito.mad.lab5g10.seekscape.firebase.TheUserModel
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.ProfilePic
import it.polito.mad.lab5g10.seekscape.models.Travel
import it.polito.mad.lab5g10.seekscape.models.User
import it.polito.mad.lab5g10.seekscape.models.UserInfoViewModel
import it.polito.mad.lab5g10.seekscape.timeAgo
import it.polito.mad.lab5g10.seekscape.ui._common.components.ActionButton
import it.polito.mad.lab5g10.seekscape.ui._common.components.AddLocation
import it.polito.mad.lab5g10.seekscape.ui._common.components.ImagePickerUtils
import it.polito.mad.lab5g10.seekscape.ui._common.components.PillButtonEditable
import it.polito.mad.lab5g10.seekscape.ui._common.components.SelectionDialog
import it.polito.mad.lab5g10.seekscape.ui._common.components.UserImageWithMenu
import it.polito.mad.lab5g10.seekscape.ui._common.components.UserReview
import it.polito.mad.lab5g10.seekscape.ui._common.personalities
import it.polito.mad.lab5g10.seekscape.ui.navigation.Actions
import it.polito.mad.lab5g10.seekscape.ui.profile.components.LandscapeUserCard
import it.polito.mad.lab5g10.seekscape.ui.profile.components.PastExperienceCard
import it.polito.mad.lab5g10.seekscape.ui.profile.components.PortraitUserCard
import kotlinx.coroutines.launch

private fun checkCameraPermission(
    context: Context,
    permissionLauncher: ActivityResultLauncher<String>,
    intentLauncher: ActivityResultLauncher<Intent>,
    vm: UserInfoViewModel
) {
    when {
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
            openCamera(vm, context, intentLauncher)
        }
        ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.CAMERA) -> {
            showRationaleDialog(context, "Camera access is required to take profile pictures") {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
        else -> {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }


    }
}


private fun checkGalleryPermission(
    context: Context,
    permissionLauncher: ActivityResultLauncher<String>,
    intentLauncher: ActivityResultLauncher<Intent>
) {
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    when {
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
            openGallery(intentLauncher)
        }
        ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission) -> {
            showRationaleDialog(context, "Storage access is needed to select photos") {
                permissionLauncher.launch(permission)
            }
        }
        else -> {
            permissionLauncher.launch(permission)
        }
    }
}


private fun showRationaleDialog(context: Context, message: String, onConfirm: () -> Unit) {
    AlertDialog.Builder(context)
        .setTitle("Permission Needed")
        .setMessage(message)
        .setPositiveButton("OK") { _, _ -> onConfirm() }
        .setNegativeButton("Cancel", null)
        .show()
}

private fun openCamera(vm: UserInfoViewModel, context: Context, launcher: ActivityResultLauncher<Intent>) {
    val photoFile = ImagePickerUtils.createImageFile(context)
    val photoUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        photoFile
    )
    vm.capturedImageUri = photoUri

    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
        putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
    }
    launcher.launch(takePictureIntent)
}


private fun openGallery(launcher: ActivityResultLauncher<Intent>) {
    val galleryIntent = ImagePickerUtils.getGalleryIntent()
    launcher.launch(galleryIntent)
}

@Composable
fun UserProfileScreen(
    viewModel: UserInfoViewModel,
    isOwnProfile: Boolean,
    navCont: NavHostController
){

    val context = LocalContext.current

    // Intent Launchers
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.setProfilePic(ProfilePic.Url(viewModel.capturedImageUri.toString()))
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val file = copyUriToInternalStorage(context, uri, "image_${System.currentTimeMillis()}.jpg")
                viewModel.setProfilePic(ProfilePic.Url("file://${file?.absolutePath}"))
            }
        }
    }

    // Permission Launchers
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.cameraPermissionGranted.value = isGranted
        if (isGranted) {
            openCamera(viewModel, context, cameraLauncher)
        }
    }

    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.galleryPermissionGranted.value = isGranted
        if (isGranted) {
            openGallery(galleryLauncher)
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            UserProfile(
                vm = viewModel,
                isOwnProfile = isOwnProfile,
                onRequestCameraPermission = {
                    checkCameraPermission(
                        context,
                        cameraPermissionLauncher,
                        cameraLauncher,
                        viewModel
                    )
                },
                onRequestGalleryPermission = {
                    checkGalleryPermission(
                        context,
                        galleryPermissionLauncher,
                        galleryLauncher
                    )
                },
                navCont
            )
        }
    }
}

@Composable
fun UserProfile(vm: UserInfoViewModel, isOwnProfile: Boolean, onRequestCameraPermission: () -> Unit,
                onRequestGalleryPermission: () -> Unit, navCont: NavHostController) {

    val isUserLoadedValue by vm.isUserLoadedValue.collectAsState()

    val myProfile by AppState.myProfile.collectAsState()
    val profileUserId by vm.idValue.collectAsState()

    Log.d("ProfileScreen", "UserProfile waiting")

    if(isUserLoadedValue){
        Column(Modifier.fillMaxSize()) {
            Spacer(Modifier.height(10.dp))
            if(vm.isEditing)
                EditPanel(vm = vm,
                    onRequestCameraPermission = onRequestCameraPermission,
                    onRequestGalleryPermission = onRequestGalleryPermission,
                    navCont, profileUserId)
            else
                PresentationPanel(vm,navCont)
        }

        if(myProfile.userId==profileUserId){
            var possibleNotificationId = "my_profile_review_${profileUserId}"
            LaunchedEffect(possibleNotificationId, myProfile.notifications) {
                if (possibleNotificationId.isNotEmpty() && myProfile.notifications.any { it.id == possibleNotificationId }) {
                    CommonModel.removeNotificationById(myProfile.userId, possibleNotificationId)
                }
            }
        }

    }else{
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

}

@Composable
fun UserPersonalInfo(vm: UserInfoViewModel) {
    val nameValue by vm.nameValue.collectAsState()
    val surnameValue by vm.surnameValue.collectAsState()
    val nicknameValue by vm.nicknameValue.collectAsState()
    val profilePic by vm.profilePic.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {
        if(!vm.isEditing){
            UserImage(profilePic, 125.dp, nameValue, surnameValue, fontSize = 50)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text="$nameValue $surnameValue", style = MaterialTheme.typography.headlineMedium)
            Text(text=nicknameValue, style = MaterialTheme.typography.bodyLarge)
        }
        else{                   //EDITING MODE
            Spacer(modifier = Modifier.height(12.dp))
            Row (Modifier.fillMaxWidth().padding(start = 20.dp),
                horizontalArrangement = Arrangement.Start){
                Text(text="User Datails", style = MaterialTheme.typography.titleMedium)
            }
            OutlinedTextField(
                value = nicknameValue,
                onValueChange = {vm.setNickName(it)},
                label = {Text("NickName", color = MaterialTheme.colorScheme.primary)},
                isError = !vm.isValid,
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp)
            )
            if(vm.nickNameError.isNotBlank())
                Text(vm.nickNameError, color = MaterialTheme.colorScheme.error)
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun UserData(numReview: Int, ratingVal: Double, numTravels: Int) {
    Column(horizontalAlignment = Alignment.Start) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(text=numReview.toString(), style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text="Reviews", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ){
            Text(text=String.format("%.1f", ratingVal), style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Rounded.Star, contentDescription = "Rating", modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text="Rating", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text=numTravels.toString(), style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text="Travels", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun UserDetails(vm: UserInfoViewModel) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 26.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        val nationalityValue by vm.nationalityValue.collectAsState()
        val cityValue by vm.cityValue.collectAsState()
        val languageValue by vm.languageValue.collectAsState()
        val ageValue by vm.ageValue.collectAsState()

        if (!vm.isEditing) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text="AGE",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text=ageValue, style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text="NATIONALITY",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text=nationalityValue, style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text="CITY",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text=cityValue, style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text="LANGUAGE",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text=languageValue, style = MaterialTheme.typography.bodyLarge)
            }
        }
        else {  //EDITING MODE
            val bio by vm.bioValue.collectAsState()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ){
//                Row (Modifier.fillMaxWidth().padding(start = 5.dp),
//                    horizontalArrangement = Arrangement.Start){
//                    Text(text="Update details", style = MaterialTheme.typography.titleMedium)
//                }
                OutlinedTextField(
                    value = nationalityValue,
                    onValueChange = {vm.setNationality(it)},
                    label = {Text("Nationality", color = MaterialTheme.colorScheme.primary)},
                    isError = !vm.isValid,
                    modifier = Modifier.fillMaxWidth().padding(start = 5.dp, end = 25.dp)
                )
                if(vm.nationalityError.isNotBlank())
                    Text(vm.nationalityError, color = MaterialTheme.colorScheme.error)

                OutlinedTextField(
                    value = cityValue,
                    onValueChange = {vm.setCity(it)},
                    label = {Text("City", color = MaterialTheme.colorScheme.primary)},
                    isError = !vm.isValid,
                    modifier = Modifier.fillMaxWidth().padding(start = 5.dp, end = 25.dp)
                )
                if(vm.cityError.isNotBlank())
                    Text(vm.cityError, color = MaterialTheme.colorScheme.error)

                OutlinedTextField(
                    value = languageValue,
                    onValueChange = {vm.setLanguage(it)},
                    label = {Text("Languages", color = MaterialTheme.colorScheme.primary)},
                    isError = !vm.isValid,
                    modifier = Modifier.fillMaxWidth().padding(start = 5.dp, end = 25.dp)
                )
                if(vm.languageError.isNotBlank())
                    Text(vm.languageError, color = MaterialTheme.colorScheme.error)

                Spacer(modifier = Modifier.height(15.dp))
                Row (Modifier.fillMaxWidth().padding(start = 5.dp),
                    horizontalArrangement = Arrangement.Start){
                    Text(text="Bio", style = MaterialTheme.typography.titleMedium)
                }
                OutlinedTextField(
                    value = bio ?: "",
                    onValueChange = { vm.setBio(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(start = 5.dp, end = 25.dp),
                    placeholder = { Text("Enter your bio...", fontSize = 16.sp) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditableUserPersonality(vm: UserInfoViewModel){
    var showDialog by remember { mutableStateOf(false) }
    val personality by vm.personality.collectAsState()

    Column(
        Modifier.fillMaxWidth()
            .padding(start = 26.dp),
        horizontalAlignment = Alignment.Start
    ){
        Row(horizontalArrangement = Arrangement.Start){
            Text(text="Personality", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(5.dp))
        FlowRow (
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ){
            personality.forEach {
                PillButtonEditable(it) { vm.removePersonality(it) }
            }
        }
        TextButton(
            onClick = { showDialog = true}
        ){
            Text(text="select more", style = MaterialTheme.typography.bodySmall)
        }

        if(showDialog){
            SelectionDialog("Personalty",personality, personalities, {selected -> vm.addPersonality(selected)}, { showDialog = false })
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserPersonality(elements: List<String>){

    Column(
        Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ){
        Row(horizontalArrangement = Arrangement.Start){
            Text(text="Personality", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(5.dp))
        FlowRow (
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ){
            elements.forEach {
                PillButton(it)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserDestinations(elements: List<String>?){
    Column(
        Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ){
        Row(horizontalArrangement = Arrangement.Start){
            Text(text="Most desired destinations", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(5.dp))
        if (elements != null){
            FlowRow (
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ){
                elements.forEach {
                    PillButton(it)
                }
            }
        }
        else {
            Text("No desired destinations found", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditableUserDestinations(vm: UserInfoViewModel, showScreen: MutableState<Boolean> = remember { mutableStateOf(false) }) {
    val desiredDestinations by vm.desiredDestinations.collectAsState()

    Column(
        Modifier.fillMaxWidth()
            .padding(start = 26.dp),
        horizontalAlignment = Alignment.Start
    ){
        Row(horizontalArrangement = Arrangement.Start){
            Text(text="Most desired destinations", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(5.dp))
        if (desiredDestinations != null){
            FlowRow (
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ){
                desiredDestinations?.forEach {
                    PillButtonEditable(it) { vm.removeLocation(it) }
                }
            }
        }
        else {
            Text("No desired destinations found", style = MaterialTheme.typography.titleMedium)
        }
        TextButton(
            onClick = { showScreen.value = true }
        ){
            Text(text="select more", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun EditPanel(vm: UserInfoViewModel, onRequestCameraPermission: () -> Unit,
              onRequestGalleryPermission: () -> Unit, navCont: NavHostController, userId:String){
    val actions = remember(navCont) { Actions(navCont) }
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current

    val nicknameValue by vm.nicknameValue.collectAsState()
    val nameValue by vm.nameValue.collectAsState()
    val surnameValue by vm.surnameValue.collectAsState()
    val profilePic by vm.profilePic.collectAsState()
    val desiredDestinations by vm.desiredDestinations.collectAsState()
    val nationalityValue by vm.nationalityValue.collectAsState()
    val cityValue by vm.cityValue.collectAsState()
    val languageValue by vm.languageValue.collectAsState()
    val personality by vm.personality.collectAsState()
    val bio by vm.bioValue.collectAsState()
    val showLocationScreen = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val theUserModel = TheUserModel()
    if (showLocationScreen.value) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            AddLocation(
                onCancel = { showLocationScreen.value = false },
                onLocationSelected = { location ->
                    vm.addLocation(location.name)
                    showLocationScreen.value = false
                },
            )
        }
    }else{
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                UserImageWithMenu(profilePic, 150.dp, modifier = Modifier,vm,
                    onSelectFromGallery = {
                        onRequestGalleryPermission()
                    },
                    onTakePhoto = {
                        onRequestCameraPermission()
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(text="$nameValue $surnameValue", style = MaterialTheme.typography.headlineMedium)
                Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            UserPersonalInfo(vm)
                        }
                        Spacer(Modifier.width(20.dp))
                    }

                    UserDetails(vm)

                    Spacer(Modifier.height(20.dp))

                    EditableUserPersonality(vm)

                    Spacer(Modifier.height(10.dp))

                    EditableUserDestinations(vm,showLocationScreen)

                    Row(Modifier.fillMaxWidth()) {
                        ActionButton("Save") {
                            if(vm.validate()){
                                val user = User(
                                    userId = userId,
                                    nickname = nicknameValue,
                                    name = nameValue,
                                    surname = surnameValue,
                                    phoneNumber = "",
                                    email = "",
                                    profilePic = profilePic,
                                    bio = bio,
                                    travelPreferences = listOf(),
                                    desiredDestinations = desiredDestinations,
                                    age = 0,
                                    nationality = nationalityValue,
                                    city = cityValue,
                                    language = languageValue,
                                    numTravels = 0,
                                    personality = personality,
                                    reviews = listOf(),
                                )
                                lifecycleOwner.lifecycleScope.launch {
                                    try {
                                        theUserModel.updateMyProfile(user)
                                        val myProfile = CommonModel.getUser(user.userId)
                                        if (myProfile != null) {
                                            AppState.updateMyProfile(myProfile)
                                            actions.navigateBack()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Error during update profile, try again later",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PortraitLayout(vm: UserInfoViewModel, navCont: NavHostController){
    val reviews by vm.reviews.collectAsState()
    val pastExperiences by vm.trips.collectAsState()
    val actions = remember(navCont) { Actions(navCont)}
    val bio by vm.bioValue.collectAsState()
    val theUserModel = TheUserModel()
    val coroutineScope = rememberCoroutineScope()

    Column(
        Modifier.fillMaxSize().padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PortraitUserCard(vm)
        if(bio!!.isNotBlank()){
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top=20.dp, bottom = 10.dp).height(0.dp),
                color = MaterialTheme.colorScheme.outline
            )
            Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)){
                Text(text="Bio", style = MaterialTheme.typography.headlineMedium)
            }
            Text(
                text = bio!!,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top=20.dp, bottom = 10.dp).height(0.dp),
            color = MaterialTheme.colorScheme.outline
        )
        Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)){
            Text(text="My reviews", style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(Modifier.height(10.dp))
        Row (
            Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp)
        ){
            if(reviews != null && (reviews as List<Any?>).isNotEmpty()){
                reviews!!.map {
                    UserReview(it.author, timeAgo(it.date), it.reviewText, it.rating, navCont)
                    Spacer(Modifier.width(16.dp))
                }
            }
            else{
                Text("No reviews found", style = MaterialTheme.typography.titleMedium)
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top=20.dp, bottom = 10.dp).height(0.dp),
            color = MaterialTheme.colorScheme.outline
        )
        Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)){
            Text(text="My past experiences", style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(Modifier.height(16.dp))
        Row (
            Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp)
        ){
            if(pastExperiences!=null && (pastExperiences as List<Travel?>).isNotEmpty()){
                pastExperiences!!.map {
                    PastExperienceCard(it!!, {actions.seeTravel(it.travelId)})
                    Spacer(Modifier.width(16.dp))
                }
            }else{
                Text("No past experiences found", style = MaterialTheme.typography.titleMedium)
            }

        }
        Spacer(Modifier.height(16.dp))

    }

}

@Composable
fun LandscapeLayout(vm: UserInfoViewModel, navCont: NavHostController){
    val reviews by vm.reviews.collectAsState()
    val pastExperiences by vm.trips.collectAsState()
    val actions = remember(navCont) { Actions(navCont) }
    val bio by vm.bioValue.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        LandscapeUserCard(vm)
        if(bio!!.isNotBlank()){
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top=20.dp, bottom = 10.dp).height(0.dp),
                color = MaterialTheme.colorScheme.outline
            )
            Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)){
                Text(text="Bio", style = MaterialTheme.typography.headlineMedium)
            }
            Text(
                text = bio!!,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top=20.dp, bottom = 10.dp).height(0.dp),
            color = MaterialTheme.colorScheme.outline
        )
        Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)){
            Text(text="My reviews", style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(Modifier.height(10.dp))
        Row (
            Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp)
        ){
            if(reviews != null && (reviews as List<Any?>).isNotEmpty()){
                reviews!!.map {
                    UserReview(it.author, timeAgo(it.date), it.reviewText, it.rating, navCont)
                    Spacer(Modifier.width(16.dp))
                }
            }
            else{
                Text("No reviews found", style = MaterialTheme.typography.titleMedium)
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top=20.dp, bottom = 10.dp).height(0.dp),
            color = MaterialTheme.colorScheme.outline
        )
        Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)){
            Text(text="My past experiences", style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(Modifier.height(16.dp))
        Row (
            Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp)
        ){

            if(pastExperiences!=null && (pastExperiences as List<Travel?>).isNotEmpty()){
                pastExperiences!!.map {
                    PastExperienceCard(it!!, {actions.seeTravel(it.travelId)})
                    Spacer(Modifier.width(16.dp))
                }
            }else{
                Text("No past experiences found", style = MaterialTheme.typography.titleMedium)
            }

        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun PresentationPanel(vm: UserInfoViewModel, navCont: NavHostController) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    if (isLandscape){
        LandscapeLayout(vm, navCont)
    }
    else {
        PortraitLayout(vm, navCont)
    }
}

