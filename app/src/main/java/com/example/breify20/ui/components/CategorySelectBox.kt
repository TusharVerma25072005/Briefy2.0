package com.example.breify20.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.breify20.ui.screens.EmailPriority

@Composable
fun CategorySelectBox(
    selected: EmailPriority,
    onSelect: (EmailPriority) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        EmailPriority.entries.forEach { priority ->
            FilterChip(
                selected = selected == priority,
                onClick = { onSelect(priority) },
                label = {
                    Text(priority.name)
                }
            )
        }
    }
}
@Composable
@Preview(showBackground = true)
fun CategorySelectBoxPreview(){
    CategorySelectBox(
        selected = EmailPriority.HIGH,
        onSelect = {}
    )

}