package id.co.ingatin.ui.screen.task

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.co.ingatin.ui.common.UiState
import id.co.ingatin.ui.components.CardMyTask
import id.co.ingatin.ui.components.ErrorContent
import id.co.ingatin.ui.components.HomeTabs
import id.co.ingatin.ui.components.LoadingContent
import id.co.ingatin.ui.components.headerTask
import id.co.ingatin.ui.theme.IngatinTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyTaskScreen(
    navController: NavController,
    viewModel: TaskViewModel = hiltViewModel(),
    category: String
) {

    val taskState by viewModel.taskState.collectAsState()
    val categoryOptionState by viewModel.categoryOption.collectAsState()

    var selectedCategory by remember { mutableStateOf(category) }

    val tabCategories = listOf("All") + when (val state = categoryOptionState) {
        is UiState.Success -> state.data.map { it.name }
        else -> emptyList()
    }

    LaunchedEffect(Unit) {
        viewModel.getCategoryOptions()
        viewModel.getAllTasks()
    }

    LaunchedEffect(selectedCategory) {
        viewModel.setCategory(selectedCategory)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            headerTask(
                titleHeader = "My Task",
                navController = navController
            )

            Spacer(modifier = Modifier.height(31.dp))

            HomeTabs(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                categories = tabCategories
            )
            Spacer(modifier = Modifier.height(16.dp))


            when (val state = taskState) {
                is UiState.Success -> {
                    val filteredTasks = if (selectedCategory == "All") {
                        state.data
                    } else {
                        state.data.filter { it.category == selectedCategory }
                    }
                    if (filteredTasks.isEmpty()) {
                        Text(text = "No tasks available")
                    }
                    filteredTasks.forEach { task ->
                        CardMyTask(
                            tasks = task,
                            modifier = Modifier
                                .padding(bottom = 8.dp),
                            onClick = {
                                navController.navigate("DetailTask/${task.id}")
                            }
                        )
                    }
                }
                is UiState.Loading -> {
                    LoadingContent()
                }

                is UiState.Error -> {
                    ErrorContent(
                        message = state.errorMessage,
                        onRetry = { viewModel.getAllTasks() }
                    )
                }

                is UiState.Empty -> {
                    Text(text = "No tasks available")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyTaskScreenPreview() {
    IngatinTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
//            MyTaskScreen(navController = navController)
        }
    }
}
