package it.polito.mad.lab5g10.seekscape.ui._common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun <T> SelectionDialog(title: String, selectedOptions: List<T>, options: List<T>, addSelections: (selectedOptions: List<T>) -> Unit, closeDialog: () -> Unit, getIcon: (T) -> ImageVector? = { null }) {
    val selected = remember { mutableStateListOf<T>().apply { addAll(selectedOptions) } }

    Dialog(
        onDismissRequest = {
            closeDialog()
        }
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text=title, style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    options.forEach { option ->
                        val isSelected = option in selected
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    if (isSelected) {
                                        selected.remove(option)
                                    } else {
                                        selected.add(option)
                                    }
                                }
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = {
                                    if (it) {
                                        selected.add(option)
                                    } else {
                                        selected.remove(option)
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary,
                                    uncheckedColor = MaterialTheme.colorScheme.primary,
                                    checkmarkColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            val icon = getIcon(option)
                            Text(text=option.toString(), style = MaterialTheme.typography.bodyLarge, modifier = if (icon != null) Modifier.weight(5f) else Modifier.weight(1f))
                            if (icon != null){
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.outline
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = {
                        closeDialog()
                    }) {
                        Text("Back", style = MaterialTheme.typography.titleMedium,color = MaterialTheme.colorScheme.secondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        addSelections(selected)
                        closeDialog()
                    }) {
                        Text("Save", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}
