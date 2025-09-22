package id.co.brainy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun FilterTask(
    selectedOption: String,
    onOptionSelected: (String) -> Unit){
    var expanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = selectedOption,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
            ),
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .clickable {
                    expanded = true
                }
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "More options",
            modifier = Modifier
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOf("All Task", "Academy", "Work").forEach{ option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )

            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun FilterTaskPreview() {
//    FilterTask()
}