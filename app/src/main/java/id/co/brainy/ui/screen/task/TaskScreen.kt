package id.co.brainy.ui.screen.task

import android.widget.Toast
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.co.brainy.R
import id.co.brainy.ui.ViewModelFactory
import id.co.brainy.ui.common.UiState
import id.co.brainy.ui.components.ButtonCategory
import id.co.brainy.ui.components.headerTask
import id.co.brainy.ui.theme.BrainyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    navController: NavController,
    taskId: String? = null
) {

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var selectCategory by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Work", "Academy")

    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context) }
    val viewModel: TaskViewModel = viewModel(factory = factory)

    val createTask by viewModel.createTask.collectAsState()

    val date by viewModel.date.observeAsState("")
    val time by viewModel.time.observeAsState("")

    val dateTime by viewModel.dateTime.observeAsState("")

    val taskDetailState by viewModel.taskDetail.collectAsState()

    val showDialog = remember { mutableStateOf(false) }


    LaunchedEffect(taskId) {
        taskId?.let {
            viewModel.getTaskById(it)
        }
    }

    LaunchedEffect(taskDetailState) {
        if (taskId != null) {
            when (val state = taskDetailState) {
                is UiState.Success -> {
                    state.data?.firstOrNull()?.let { task ->
                        title = task.title ?: ""
                        description = task.desc ?: ""
                        selectCategory = task.category ?: ""
                        // Format waktu & tanggal jika datanya tersedia
                        task.dueDate?.let { dueDate ->
                            val parts = dueDate.split(" ")
                            if (parts.size == 2) {
                                viewModel.setDate(parts[0])
                                viewModel.setTime(parts[1])
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    Toast.makeText(context, "Failed to load task", Toast.LENGTH_SHORT).show()
                }

                else -> Unit
            }
        }
    }

    val editTask by viewModel.editTask.collectAsState()

    LaunchedEffect(editTask) {
        if (taskId != null) {
            when (val state = editTask) {
                is UiState.Success -> {
                    Toast.makeText(context, "Task updated!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }

                is UiState.Error -> {
                    Toast.makeText(context, "Failed: ${state.errorMessage}", Toast.LENGTH_SHORT).show()
                }

                else -> Unit
            }
        }
    }


    LaunchedEffect(createTask) {
        when (val state = createTask) {
            is UiState.Success -> {
                Toast.makeText(context, "Task created!", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }

            is UiState.Error -> {
                Toast.makeText(context, "Failed: ${state.errorMessage}", Toast.LENGTH_SHORT).show()
            }

            else -> Unit
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Text(
                    text = if (taskId != null) "Confirm Update" else "Confirm Create",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                )
            },
            text = {
                Text(
                    text = if (taskId != null)
                        "Are you sure you want to update this task?"
                    else
                        "Are you sure you want to create this task?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        val selectedCategory = selectCategory ?: ""
                        val finalDateTime = dateTime ?: ""

                        if (taskId != null) {
                            viewModel.editTask(
                                taskId = taskId,
                                category = selectedCategory,
                                dueDate = finalDateTime,
                                title = title,
                                desc = description,
                                context = context
                            )
                        } else {
                            viewModel.createTask(
                                category = selectedCategory,
                                dueDate = finalDateTime,
                                title = title,
                                desc = description,
                                context = context
                            )
                        }
                    }
                ) {
                    Text(
                        text = if (taskId != null) "Update" else "Create",
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
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        )
    }




    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),

        ) {
        headerTask(
            titleHeader = if (taskId != null) "Edit Task" else "Create Task",
            navController = navController
        )
        Spacer(modifier = Modifier.height(31.dp))
        TitleTextField("Title")
        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
            },
            placeholder = {
                Text(
                    text = "Title", color = Color.LightGray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 22.dp, top = 4.dp)
                .border(
                    width = 2.dp, color = Color.LightGray, shape = RoundedCornerShape(14.dp)
                ),
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )
        TitleTextField("Deadline")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 22.dp, start = 8.dp, end = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = time,
                onValueChange = {},
                placeholder = { Text("Time") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = {
                        viewModel.selectTime(context)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_clock),
                            contentDescription = "Select Time",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                modifier = Modifier
                    .weight(0.4F)
                    .border(
                        width = 2.dp, color = Color.LightGray, shape = RoundedCornerShape(14.dp)
                    ),
                shape = RoundedCornerShape(14.dp),
            )
            OutlinedTextField(
                value = date,
                onValueChange = {},
                placeholder = { Text("Date") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = {
                        viewModel.selectDate(context)
                    }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    }
                },
                modifier = Modifier
                    .weight(0.6F)
                    .border(
                        width = 2.dp, color = Color.LightGray, shape = RoundedCornerShape(14.dp)
                    ),
                shape = RoundedCornerShape(14.dp),
            )

        }

        TitleTextField("Category")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 22.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            categories.forEach { category ->
                ButtonCategory(
                    btnTitle = category,
                    onCategoryClick = { selected ->
                        selectCategory = selected
                    },
                    isSelected = selectCategory == category
                )
            }
        }
        TitleTextField("Description")
        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
            },
            placeholder = {
                Text(
                    text = "Enter description", color = Color.LightGray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(top = 4.dp, bottom = 32.dp)
                .border(
                    width = 2.dp, color = Color.LightGray, shape = RoundedCornerShape(14.dp)
                ),
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = false,
        )
        Button(
            onClick = {
                showDialog.value = true
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = if (taskId != null) "Update Task" else "Create Task",
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


@Composable
fun TitleTextField(title: String) {
    Text(
        text = title, style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.tertiary
        ), modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)

    )

}


@Preview(showBackground = true)
@Composable
fun TaskScreenPreview() {
    BrainyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            TaskScreen(navController)
        }
    }
}