package it.polito.mad.lab5g10.seekscape.ui.profile

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import it.polito.mad.lab5g10.seekscape.firebase.TheUserModel
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.services.AccountService
import it.polito.mad.lab5g10.seekscape.ui.navigation.MainDestinations
import kotlinx.coroutines.launch

@Composable
fun AnimatedCheckmark(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    circleColor: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = 6.dp
) {
    val pathProgress = remember { Animatable(0f) }
    val checkColor = MaterialTheme.colorScheme.onPrimary

    LaunchedEffect(Unit) {
        pathProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Canvas(modifier = modifier.size(size)) {
        drawCircle(
            color = circleColor,
            center = center,
            radius = size.toPx() / 2
        )

        val checkPath = Path().apply {
            val start = Offset(size.toPx() * 0.28f, size.toPx() * 0.52f)
            val mid = Offset(size.toPx() * 0.45f, size.toPx() * 0.68f)
            val end = Offset(size.toPx() * 0.72f, size.toPx() * 0.38f)
            moveTo(start.x, start.y)
            lineTo(mid.x, mid.y)
            lineTo(end.x, end.y)
        }

        val pathMeasure = PathMeasure()
        pathMeasure.setPath(checkPath, false)
        val pathLength = pathMeasure.length

        val animatedPath = Path()
        pathMeasure.getSegment(0f, pathProgress.value * pathLength, animatedPath, true)


        drawPath(
            path = animatedPath,
            color = checkColor,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun ResetEmailCompletedScreen(navCont: NavHostController, uid: String, email: String) {


    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uid, email) {
        Log.d("ResetEmailCompletedScreen", "uid: $uid, email: $email")

        isLoading = true
        try {
            val userModel = TheUserModel()
            userModel.updateEmail(uid, email)
        } catch (e: Exception) {
            Log.e("ResetEmailCompletedScreen", "Error updating email", e)
            isError = true
        } finally {
            isLoading = false
        }
    }
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

    } else {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(1.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isError){
                        Text(
                            text = "Error while updating email",
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                    else {
                        AnimatedCheckmark()
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Operation completed",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }

                Button(
                    onClick = {
                        if (!isError){
                            coroutineScope.launch {
                                val accountService = AccountService()
                                accountService.signOut()
                                AppState.setUserAsUnlogged()
                            }
                        }
                        navCont.navigate(MainDestinations.HOME_ROUTE)
                      },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Go back",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}
