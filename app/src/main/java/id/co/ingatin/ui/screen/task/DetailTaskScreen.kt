package id.co.ingatin.ui.screen.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import id.co.ingatin.ui.common.UiState
import id.co.ingatin.ui.components.ConfirmDialog
import id.co.ingatin.ui.components.ErrorContent
import id.co.ingatin.ui.components.LoadingContent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.co.ingatin.Routes
import id.co.ingatin.ui.components.headerTask
import id.co.ingatin.ui.theme.IngatinTheme


@Composable
fun DetailTaskScreen(
    navController: NavController, viewModel: TaskViewModel = hiltViewModel(), taskId: String
) {

    val taskDetail by viewModel.taskById.collectAsState()
    val deleteTask by viewModel.deleteTask.collectAsState()


    LaunchedEffect(taskId) {
        viewModel.getTaskById(taskId)
    }

    LaunchedEffect(deleteTask) {
        if (deleteTask is UiState.Success) {
            navController.navigate(Routes.HOME) {
                popUpTo(0)
            }
        }

    }

    val showDialog = remember { mutableStateOf(false) }


    if (showDialog.value) {
        ConfirmDialog(
            title = "Confirm Delete",
            message = "Are you sure you want to delete this task?",
            confirmText = "Delete",
            dismissText = "Cancel",
            isConfirming = deleteTask is UiState.Loading,
            onDismiss = { showDialog.value = false },
            onConfirm = {
                showDialog.value = false
                viewModel.deleteTaskById(taskId)
            }
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        when (val task = taskDetail) {
            is UiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                ) {
                    headerTask(
                        titleHeader = task.data.category, navController = navController
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = task.data.title, style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    )
                    Text(
                        text = "${task.data.date} ${task.data.time}", style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold, color = Color.Black
                        ), modifier = Modifier.padding(bottom = 32.dp, top = 8.dp)
                    )

                    Text(
                        text = task.data.description,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold, color = Color.Black
                        ),
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                navController.navigate("${Routes.TASK}/$taskId")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = "Edit", style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ), color = Color.White, modifier = Modifier.padding(6.dp)
                            )
                        }
                        Button(
                            onClick = {
                                showDialog.value = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = "Delete", style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ), color = Color.White, modifier = Modifier.padding(6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                }
            }
            is UiState.Loading, is UiState.Empty -> {
                LoadingContent(text = "Memuat data tugas...")
            }

            is UiState.Error -> {
                ErrorContent(
                    message = task.errorMessage,
                    onRetry = { viewModel.getTaskById(taskId) }
                )
            }
        }
        if (deleteTask is UiState.Loading) {
            LoadingContent(text = "Menghapus tugas...")
        }

    }

}


@Preview(showBackground = true)
@Composable
fun DetailTaskScreenPreview() {
    IngatinTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            DetailTaskScreen(
                taskId = "O6PHC9ZPBANA7R_", navController = navController
            )
        }
    }
}