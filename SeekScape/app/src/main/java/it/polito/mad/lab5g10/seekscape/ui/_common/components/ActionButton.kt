package it.polito.mad.lab5g10.seekscape.ui._common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import it.polito.mad.lab5g10.seekscape.models.Travel
import kotlinx.coroutines.launch

@Composable
fun ActionButton(actionName: String, onClick: ()->Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background.copy(alpha = 1.0f)
                    )
                )
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp,
                focusedElevation = 10.dp
            ),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(vertical = 20.dp)
        ) {
            Text(actionName,style = MaterialTheme.typography.titleMedium)
        }
    }
}


@Composable
fun ConfirmButton(acceptAction: ()->Unit){
    IconButton(onClick = acceptAction,
        modifier = Modifier
            .size(35.dp)
            .background(color = Color(0xFF4CAF50), shape = CircleShape)
            .clip(CircleShape)) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = "Confirm request",
            tint = Color.White
        )
    }
}

@Composable
fun DenyButton(denyAction: ()->Unit){
    IconButton(onClick = denyAction,
        modifier = Modifier
            .size(35.dp)
            .background(color = MaterialTheme.colorScheme.error, shape = CircleShape)
            .clip(CircleShape)) {
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "Deny request",
            tint = Color.White
        )
    }
}

@Composable
fun ButtonsSection(acceptAction: ()->Unit, denyAction: ()->Unit) {
    Row(
        modifier = Modifier
            .widthIn(min = 100.dp)
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        ConfirmButton(acceptAction)
        Spacer(modifier = Modifier.width(8.dp))
        DenyButton(denyAction)
    }
}