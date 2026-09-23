package id.co.ingatin.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FormDialog(
    title: String,
    confirmText: String,
    dismissText: String = "Batal",
    confirmEnabled: Boolean = true,
    isSubmitting: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            if (!isSubmitting) {
                onDismiss()
            }
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            )
        },
        text = {
            content()
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = confirmEnabled && !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = MaterialTheme.colorScheme.tertiary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = confirmText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    )
                }
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                enabled = !isSubmitting
            ) {
                Text(
                    text = dismissText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.background
                    )
                )
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun FormDialogPreview() {
    FormDialog(
        title = "Tambah Kategori",
        confirmText = "Tambah",
        onDismiss = {},
        onConfirm = {},
        content = {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Nama kategori") },
                singleLine = true
            )
        }
    )
}
