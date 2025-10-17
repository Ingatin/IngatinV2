package id.co.ingatin.ui.screen.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.co.ingatin.ui.common.UiState
import id.co.ingatin.ui.components.CardMyTask
import id.co.ingatin.ui.components.CardTaskItem
import id.co.ingatin.ui.components.FilterTask
import id.co.ingatin.ui.screen.viewModel.AuthViewModel
import id.co.ingatin.ui.screen.viewModel.TaskViewModel
import id.co.ingatin.ui.theme.BrainyTheme
import kotlin.collections.orEmpty


@Composable
fun HomeScreen(
    navController: NavController,
    taskViewModel: TaskViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    val taskState by taskViewModel.myTasks.collectAsState()

    val allCount by taskViewModel.allCount.collectAsState()
    val workCount by taskViewModel.workCount.collectAsState()
    val academyCount by taskViewModel.academyCount.collectAsState()

    var selectedOption by remember { mutableStateOf("All Task") }

    LaunchedEffect(selectedOption) {
        taskViewModel.getMyTasks(selectedOption)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("task")
                    },
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = Color.Black
                ) {
                    Icon(Icons.Filled.Add, "Tambah Tugas Baru")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
            ) {
                HeaderHome(navController)
                Spacer(modifier = Modifier.height(20.dp))
                CardTaskItem(
                    title = "My Task",
                    count = allCount,
                    modifier = Modifier
                        .clickable {
                            navController.navigate("MyTask/All Task")
                        }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CardTaskItem(
                        title = "Work",
                        count = workCount,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                navController.navigate("MyTask/Work")
                            }
                    )
                    CardTaskItem(
                        title = "Academy",
                        count = academyCount,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                navController.navigate("MyTask/Academy")
                            }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Tasks",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    )
                    FilterTask(
                        selectedOption = selectedOption,
                        onOptionSelected = { selectedOption = it }
                    )
                }

                when (val state = taskState) {
                    is UiState.Success -> {
                        val tasks = state.data.orEmpty()
                        Log.d("HomeScreen", "tasks: $tasks")
                        if (tasks.isEmpty()) {
                            Text(text = "No tasks available")
                        }
                        tasks.forEach { task ->
                            CardMyTask(
                                tasks = task,
                                modifier = Modifier
                                    .padding(bottom = 8.dp),
                                onClick = {
                                    navController.navigate("DetailTask/${task.taskId}")
                                }
                            )
                        }
                    }

                    is UiState.Error -> {
                        Text(text = (state.errorMessage))
                    }

                    else -> Unit
                }
            }
        }
        if (taskState is UiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false) {} // supaya tidak bisa diklik
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.background)
            }
        }
    }
}

@Composable
fun HeaderHome(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val userState by authViewModel.getUser.collectAsState()
    val logoutState by authViewModel.logoutState.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.getUser()
    }

    LaunchedEffect(logoutState) {
        if (logoutState) {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                )
            },
            text = { Text("Are you sure you want to Logout?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        authViewModel.logout()
                    }
                ) {
                    Text(
                        text = "Logout",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog.value = false }
                ) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    )
                }
            }
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (val state = userState) {
            is UiState.Success -> {
                val user = state.data.name
                Log.d("HOME SCREEN", "username: $user")

                Text(
                    text = "Hi $user",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 36.sp,

                        ),
                    modifier = Modifier.weight(1f)
                )

            }

            else -> Unit
        }
        Icon(
            imageVector = Icons.Default.ExitToApp,
            contentDescription = "Logout Icon",
            modifier = Modifier
                .size(32.dp)
                .clickable {
                    showDialog.value = true
                }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BrainyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            HomeScreen(
                navController = navController
            )
        }
    }
}