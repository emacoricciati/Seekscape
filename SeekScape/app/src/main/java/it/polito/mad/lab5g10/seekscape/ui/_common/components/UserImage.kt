package it.polito.mad.lab5g10.seekscape.ui._common.components

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import it.polito.mad.lab5g10.seekscape.models.ProfilePic
import it.polito.mad.lab5g10.seekscape.models.UserInfoViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImagePickerUtils {
    // For camera
    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            parentFile?.mkdirs()
        }
    }

    // For gallery
    fun getGalleryIntent(): Intent {
        return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
    }
}

@Composable
fun UserImageWithMenu(
    icon: ProfilePic?,
    size: Dp,
    modifier: Modifier = Modifier,
    vm: UserInfoViewModel,
    onSelectFromGallery: () -> Unit = {},
    onTakePhoto: () -> Unit = {},
) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    vm.toastMessage?.let { message ->
        LaunchedEffect(message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            vm.toastShown()
        }
    }

    val profilePic by vm.profilePic.collectAsState()
    val imagePainter = when (val image = profilePic) {
        is ProfilePic.Resource -> painterResource(id = image.resId)  // For drawable resources
        is ProfilePic.Url -> rememberAsyncImagePainter(image.value)  // For image URLs
        else -> null
    }
    Box(modifier = modifier) {
        if (profilePic != null && imagePainter != null) {
            Image(
                painter = imagePainter,
                contentDescription = "Profile image",
                modifier = Modifier
                    .size(size)
                    .clip(
                        CircleShape
                    ),
                contentScale = ContentScale.Crop
            )
        } else {
            UserImage(icon = icon, size = size)
        }
        IconButton(
            onClick = { showMenu = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(32.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)

        ) {
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = "Edit image",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        DropdownMenu(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface),
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(

                text = { Text("Select from gallery", style = MaterialTheme.typography.bodyMedium,) },
                onClick = {
                    showMenu = false
                    onSelectFromGallery()
                }
            )
            DropdownMenuItem(
                text = { Text("Take a photo", style = MaterialTheme.typography.bodyMedium,) },
                onClick = {
                    showMenu = false
                    onTakePhoto()
                }
            )
        }
    }
}

@Composable
fun UserImage(icon: ProfilePic?, size: Dp, name: String? = null, surname: String? = null, fontSize: Int = 18, modifier: Modifier = Modifier) {

    when (icon) {
        is ProfilePic.Resource -> {
            Image(
                painter = painterResource(id = icon.resId),
                contentDescription = "User Image",
                modifier = modifier
                    .size(size)
            )
    }
        is ProfilePic.Url -> {
            println("Loading image from URL: ${icon.value}")
            AsyncImage(
                model = icon.value,
                contentDescription = "User Profile Image",
                modifier = modifier
                    .size(size)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        null -> {
            if (name != null && surname != null)
                MonogramIcon(name, surname, size, fontSize)
        }
    }

}

@Composable
fun MonogramIcon(name: String, surname: String, size: Dp, fontSize: Int) {
    var monogram = ""
    if (name.isNotEmpty()){
        monogram += name.first().uppercase()
    }
    if (surname.isNotEmpty()){
        monogram += surname.first().uppercase()
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), shape = CircleShape)
    ) {
        Text(
            text = monogram,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = fontSize.sp
        )
    }
}