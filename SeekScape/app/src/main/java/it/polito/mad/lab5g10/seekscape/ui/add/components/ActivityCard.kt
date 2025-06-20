package it.polito.mad.lab5g10.seekscape.ui.add.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ActivityCard (image: ImageVector, title: String, optional: Boolean, onClick: () -> Unit = {}) {
    ElevatedCard(
        modifier = Modifier.clickable {
            onClick()
        },
        colors = CardDefaults.cardColors(
            containerColor = if (optional) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = if (optional) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
    ) {
        Row (
            modifier = Modifier.fillMaxWidth().padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row (modifier = Modifier.weight(3f), verticalAlignment = Alignment.CenterVertically){
                Icon(
                    imageVector = image,
                    contentDescription = "Activity Icon",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp)
                )
                Text(title)
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(22.dp)
                    .border(
                        width = 2.dp,
                        color = if(optional) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .background(color = Color.Transparent, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = if (!optional) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = "Optional Icon",
                    tint = if(optional) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                        .clickable {
                            onClick()
                        },
                )
            }
        }
    }
}