package id.co.ingatin.ui.screen.home

import android.net.Uri
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import id.co.ingatin.ui.common.UiState
import id.co.ingatin.ui.components.CardMyTask
import id.co.ingatin.ui.components.CardTaskItem
import id.co.ingatin.ui.components.ConfirmDialog
import id.co.ingatin.ui.components.ErrorContent
import id.co.ingatin.ui.components.FilterTask
import id.co.ingatin.ui.components.LoadingContent
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.co.ingatin.Routes
import id.co.ingatin.ui.screen.auth.AuthViewModel
import id.co.ingatin.ui.theme.IngatinTheme


@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {

    val taskState by homeViewModel.taskState.collectAsState()
    val countTask by homeViewModel.countCategories.collectAsState()
    val userState by authViewModel.getUser.collectAsState()
    val logoutState by authViewModel.logoutState.collectAsState()


    var selectedOption by remember { mutableStateOf("All") }

    val categoryData = when (val state = countTask) {
        is UiState.Success -> state.data
        // Pesan error ditampilkan melalui taskState; kritik dirender 0.
        is UiState.Error -> null
        // Loading / Empty: kritik dirender 0 sampai data tersedia.
        else -> null
    }

    val totalTask = categoryData?.allTask ?: 0
    val topCategories = categoryData?.categories.orEmpty()

    val filterCategories = when (val state = taskState) {
        is UiState.Success -> state.data.map { it.category }.distinct()
        else -> emptyList()
    }

    val greeting = when (val state = userState) {
        is UiState.Success -> state.data.name.ifBlank { "Pengguna" }
        is UiState.Loading -> "..."
        is UiState.Error, is UiState.Empty -> "Pengguna"
    }

    LaunchedEffect(Unit) {
        homeViewModel.refreshHome()
        authViewModel.getUser()
    }

    LaunchedEffect(logoutState) {
        if (logoutState is UiState.Success) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(Routes.HOME) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("${Routes.TASK}/")
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
                HeaderHome(
                    greeting = greeting,
                    onLogout = { authViewModel.logout() }
                )
                Spacer(modifier = Modifier.height(20.dp))


                CardTaskItem(
                    title = "My Task",
                    count = totalTask,
                    modifier = Modifier
                        .clickable {
                            navController.navigate("${Routes.MY_TASK}/All")
                        }
                )
                if (topCategories.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        topCategories.forEach { c ->
                            CardTaskItem(
                                title = c.name,
                                count = c.count,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        navController.navigate("${Routes.MY_TASK}/${Uri.encode(c.name)}")
                                    }
                            )
                        }
                    }
                }
                when (val task = taskState) {
                    is UiState.Success -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "List Task",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            )
                            FilterTask(
                                selectedOption = selectedOption,
                                onOptionSelected = { selectedOption = it },
                                categories = filterCategories
                            )
                        }
                        val filteredTasks = if (selectedOption == "All") {
                            task.data
                        } else {
                            task.data.filter { it.category == selectedOption }
                        }
                        if (filteredTasks.isEmpty()) {
                            Text(text = "No tasks available")
                        } else {
                            filteredTasks.forEach { task ->
                                CardMyTask(
                                    tasks = task,
                                    modifier = Modifier
                                        .padding(bottom = 8.dp),
                                    onClick = {
                                        navController.navigate("${Routes.DETAIL_TASK}/${task.id}")
                                    }
                                )
                            }
                        }
                    }

                    is UiState.Error -> {
                        ErrorContent(
                            message = task.errorMessage,
                            onRetry = { homeViewModel.refreshHome() }
                        )
                    }

                    is UiState.Empty -> {
                        Text(text = "No tasks available")
                    }

                    is UiState.Loading -> {
                        LoadingContent()
                    }
                }
            }
        }
        if(logoutState is UiState.Loading){
            LoadingContent()
        }
    }
}

@Composable
fun HeaderHome(
    greeting: String,
    onLogout: () -> Unit
) {
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        ConfirmDialog(
            title = "Logout",
            message = "Are you sure you want to Logout?",
            confirmText = "Logout",
            dismissText = "Cancel",
            onDismiss = { showDialog.value = false },
            onConfirm = {
                showDialog.value = false
                onLogout()
            }
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Hi $greeting",
            style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 28.sp
            ),
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
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
    IngatinTheme {
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