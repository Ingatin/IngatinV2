package id.co.ingatin.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Dismiss")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(timePickerState.hour, timePickerState.minute)
            })
            {
                Text("OK")
            }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onConfirm: (year: Int, month: Int, day: Int) -> Unit,
    onDismiss: () -> Unit
) {

    val currentDate = Calendar.getInstance()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = currentDate.timeInMillis
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Dismiss")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let {
                    currentDate.timeInMillis = it
                    val year = currentDate.get(Calendar.YEAR)
                    val month = currentDate.get(Calendar.MONTH) + 1
                    val day = currentDate.get(Calendar.DAY_OF_MONTH)
                onConfirm(year,month,day)
                }
            }) {
                Text("OK")
            }
        },
        text = {
            DatePicker(state = datePickerState)
        }
    )
}