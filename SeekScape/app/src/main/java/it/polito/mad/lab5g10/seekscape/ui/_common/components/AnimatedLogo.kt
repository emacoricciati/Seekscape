package it.polito.mad.lab5g10.seekscape.ui._common.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import kotlinx.coroutines.delay
import androidx.compose.ui.res.painterResource
import it.polito.mad.lab5g10.seekscape.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import it.polito.mad.lab5g10.seekscape.models.AppState
import kotlinx.coroutines.launch


@Composable
fun AnimatedLogo(onAnimationFinished: () -> Unit) {
    val scale = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    val isDarkMode = AppState.isDarkMode.collectAsState().value


    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(1f, animationSpec = tween(1000, easing = FastOutSlowInEasing))
        }
        launch {
            rotation.animateTo(360f, animationSpec = tween(1000))
        }
        delay(1500)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = if (isDarkMode==false) {
                painterResource(id = R.drawable.icon_logo)
            } else {
                painterResource(id = R.drawable.icon_logo_dark_mode)
            },
            contentDescription = "App Logo",
            modifier = Modifier
                .scale(scale.value)
                .rotate(rotation.value)
        )

    }



}
