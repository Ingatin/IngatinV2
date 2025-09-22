package id.co.brainy.ui.screen.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.co.brainy.ui.ViewModelFactory
import id.co.brainy.ui.common.UiState
import id.co.brainy.ui.components.CardMyTask
import id.co.brainy.ui.components.CardTaskItem
import id.co.brainy.ui.components.FilterTask
import id.co.brainy.ui.screen.auth.AuthViewModel
import id.co.brainy.ui.theme.BrainyTheme


@Composable
fun HomeScreen(
    navController: NavController
) {

    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context) }
    val viewModel: HomeViewModel = viewModel(factory = factory)

    var selectedOption by remember { mutableStateOf("All Task") }



    val taskList by if (selectedOption == "All Task") {
        viewModel.taskAll.collectAsState()
    }else{
        viewModel.taskCategory.collectAsState()
    }
    Log.d("selectedOption", selectedOption)


    LaunchedEffect(selectedOption) {
        if (selectedOption == "All Task") {
            viewModel.getAllTasks()
        } else {
            viewModel.getTasksByCategory(selectedOption)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getAllTasks()
    }

    val taskAllState by viewModel.taskAll.collectAsState()

    val allCount = (taskAllState as? UiState.Success)?.data?.size ?: 0
    val workCount = (taskAllState as? UiState.Success)?.data?.count { it.category == "Work" } ?: 0
    val academyCount = (taskAllState as? UiState.Success)?.data?.count { it.category == "Academy" } ?: 0


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

            when (taskList) {
                is UiState.Success -> {
                    val tasks = (taskList as UiState.Success).data.orEmpty()
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
                is UiState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                is UiState.Error -> {
                    Text(text = (taskList as UiState.Error).errorMessage)
                }

                else -> {
                    Text("Belum ada data")
                }
            }


        }
    }
}

@Composable
fun HeaderHome(navController: NavController) {
    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context) }
    val viewModel: AuthViewModel = viewModel(factory = factory)

    val username by viewModel.username.collectAsState()
    val logoutState by viewModel.logoutState.collectAsState()

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
                        viewModel.logout()
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

    LaunchedEffect(Unit) {
        viewModel.getUser()
    }

    LaunchedEffect(logoutState) {
        if (logoutState) {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Hi $username",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 36.sp,

                ),
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ExitToApp,
            contentDescription = "Logout Icon",
            modifier = Modifier
                .size(32.dp)
                .clickable {
//                    ShowSimpleNotification(context)
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