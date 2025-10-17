package id.co.ingatin.ui.screen.task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.co.ingatin.ui.common.UiState
import id.co.ingatin.ui.components.CardMyTask
import id.co.ingatin.ui.components.HomeTabs
import id.co.ingatin.ui.components.headerTask
import id.co.ingatin.ui.screen.viewModel.TaskViewModel
import id.co.ingatin.ui.theme.BrainyTheme

@Composable
fun MyTaskScreen(
    navController: NavController,
    viewModel: TaskViewModel = hiltViewModel(),
    selectedCategoryFromNav: String
) {

    val context = LocalContext.current

    val taskList by viewModel.myTasks.collectAsState()

    var selectedCategory by remember { mutableStateOf(selectedCategoryFromNav) }

//    val taskList by if (selectedCategory == "All Task") {
//        viewModel.taskAll.collectAsState()
//    } else {
//        viewModel.taskCategory.collectAsState()
//    }


//    LaunchedEffect(selectedCategory) {
//        if (selectedCategory == "All Task") {
//            viewModel.getAllTasks()
//        } else {
//            viewModel.getTasksByCategory(selectedCategory)
//        }
//    }

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
            onCategorySelected = { selectedCategory = it }
        )
        Spacer(modifier = Modifier.height(16.dp))


        when (taskList) {
            is UiState.Success -> {
                val tasks = (taskList as UiState.Success).data
                tasks.forEach { task ->
                    CardMyTask(
                        tasks = task,
                        modifier = Modifier
                            .padding(bottom = 8.dp),
                        onClick = {
//                            navController.navigate("DetailTask/${task.taskId}")
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

@Preview(showBackground = true)
@Composable
fun MyTaskScreenPreview() {
    BrainyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
//            MyTaskScreen(navController = navController)
        }
    }
}
