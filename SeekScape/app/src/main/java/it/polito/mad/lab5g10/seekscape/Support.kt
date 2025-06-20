package it.polito.mad.lab5g10.seekscape

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import it.polito.mad.lab5g10.seekscape.firebase.CommonModel
import kotlinx.coroutines.launch

data class ButtonInfo(val text: String, val onClick: () -> Unit)


@Composable
fun Support() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val resetDB = ButtonInfo("RESET DB") {
        scope.launch {
            val done = CommonModel.resetDB(context)
            done.onSuccess {
                feedbackButton(context, "DONE")
            }.onFailure { exception ->
                feedbackButton(context, "FAILED", exception = exception)
            }
        }
    }

    val buttonsData = listOf(
        resetDB,
    )

    ButtonsFunctions(buttonsInfoList = buttonsData)
}


fun feedbackButton(context: Context, message: String, exception: Throwable?=null){
    if(exception!=null)
        println("$message : \n $exception")
    else
        println(message)

    Toast.makeText(
        context,
        message,
        Toast.LENGTH_SHORT
    ).show()
}

@Composable
fun ButtonsFunctions(
    modifier: Modifier = Modifier,
    buttonsInfoList: List<ButtonInfo>
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Support QUERIES",
                modifier = Modifier.padding(bottom = 30.dp),
                style = MaterialTheme.typography.displayLarge,
            )

            buttonsInfoList.forEach { buttonInfo ->
                Button(
                    onClick = buttonInfo.onClick,
                    modifier = Modifier.padding(bottom = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = buttonInfo.text,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}