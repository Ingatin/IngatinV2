package id.co.ingatin.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String = "Konfirmasi",
    dismissText: String = "Batal",
    isConfirming: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            if (!isConfirming) {
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
            Text(text = message)
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isConfirming
            ) {
                if (isConfirming) {
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
                enabled = !isConfirming
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
fun ConfirmDialogPreview() {
    ConfirmDialog(
        title = "Hapus Tugas",
        message = "Apakah Anda yakin ingin menghapus tugas ini?",
        confirmText = "Hapus",
        onDismiss = {},
        onConfirm = {}
    )
}
