package id.co.ingatin.ui.screen.task

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import id.co.ingatin.ui.components.ButtonCategory
import id.co.ingatin.ui.components.ConfirmDialog
import id.co.ingatin.ui.components.CustomTextField
import id.co.ingatin.ui.components.FormDialog
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.co.ingatin.R
import id.co.ingatin.data.model.Category
import id.co.ingatin.data.model.FormTask
import id.co.ingatin.ui.common.UiState
import id.co.ingatin.ui.components.TimePickerDialog
import id.co.ingatin.ui.components.DatePickerDialog
import id.co.ingatin.ui.components.ErrorContent
import id.co.ingatin.ui.components.LoadingContent
import id.co.ingatin.ui.components.headerTask
import id.co.ingatin.ui.theme.IngatinTheme

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    navController: NavController,
    viewModel: TaskViewModel = hiltViewModel(),
    taskId: String = ""
) {
    val context = LocalContext.current

    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val date by viewModel.selectDate.collectAsState()
    val time by viewModel.selectTime.collectAsState()


    val categoryOptionsState by viewModel.categoryOption.collectAsState()
    val categories = when (val state = categoryOptionsState) {
        is UiState.Success -> state.data
        else -> emptyList()
    }
    val selectCategory by viewModel.selectCategory.collectAsState()

    val showDialog = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }
    val showDatePicker = remember { mutableStateOf(false) }

    val categoryToDelete = remember { mutableStateOf<Category?>(null) }
    val showAddCategoryDialog = remember { mutableStateOf(false) }
    val newCategoryName = remember { mutableStateOf("") }

    val createTaskState by viewModel.createTaskState.collectAsState()
    val taskState by viewModel.taskById.collectAsState()
    val updateTaskState by viewModel.updateTaskState.collectAsState()
    val categoryState by viewModel.categoryState.collectAsState()
    val deleteCategoryState by viewModel.deleteCategory.collectAsState()

    val titleError by viewModel.titleError.collectAsState()
    val categoryError by viewModel.categoryError.collectAsState()
    val dateError by viewModel.dateError.collectAsState()
    val timeError by viewModel.timeError.collectAsState()

    LaunchedEffect(createTaskState) {
        when (val state = createTaskState) {
            is UiState.Success -> {
                showDialog.value = false
                navController.popBackStack()
                Toast.makeText(context, "Task created!", Toast.LENGTH_SHORT).show()
            }

            is UiState.Error -> {
                Toast.makeText(context, "Failed: ${state.errorMessage}", Toast.LENGTH_SHORT).show()
            }

            else -> Unit
        }
    }

    LaunchedEffect(updateTaskState) {
        when (val state = updateTaskState) {
            is UiState.Success -> {
                showDialog.value = false
                navController.popBackStack()
                Toast.makeText(context, "Task updated!", Toast.LENGTH_SHORT).show()
            }
            is UiState.Error -> {
                Toast.makeText(context, "Failed: ${state.errorMessage}", Toast.LENGTH_SHORT).show()
            }
            else -> Unit

        }
    }

    LaunchedEffect(taskId) {
        viewModel.getTaskById(taskId)
    }

    LaunchedEffect(Unit) {
        viewModel.getCategoryOptions()
    }

    LaunchedEffect(categoryState) {
        when (val state = categoryState) {
            is UiState.Success -> {
                Toast.makeText(context, state.data, Toast.LENGTH_SHORT).show()
                viewModel.getCategoryOptions()
                showAddCategoryDialog.value = false
                newCategoryName.value = ""
            }
            is UiState.Error -> {
                Toast.makeText(context, "Failed: ${state.errorMessage}", Toast.LENGTH_SHORT).show()
                showAddCategoryDialog.value = false
                newCategoryName.value = ""
            }
            else -> Unit
        }
    }

    LaunchedEffect(deleteCategoryState) {
        when (val state = deleteCategoryState) {
            is UiState.Success -> {
                Toast.makeText(context, state.data, Toast.LENGTH_SHORT).show()
                viewModel.getCategoryOptions()
                categoryToDelete.value = null
            }
            is UiState.Error -> {
                Toast.makeText(context, "Failed: ${state.errorMessage}", Toast.LENGTH_SHORT).show()
                categoryToDelete.value = null
            }
            else -> Unit
        }
    }


    if (showDialog.value) {
        val isSubmitting = createTaskState is UiState.Loading || updateTaskState is UiState.Loading
        ConfirmDialog(
            title = if (taskId.isNotEmpty()) "Confirm Update" else "Confirm Create",
            message = if (taskId.isNotEmpty())
                "Are you sure you want to update this task?"
            else
                "Are you sure you want to create this task?",
            confirmText = if (taskId.isNotEmpty()) "Update" else "Create",
            dismissText = "Cancel",
            isConfirming = isSubmitting,
            onDismiss = { showDialog.value = false },
            onConfirm = {
                if (taskId.isNotEmpty()) {
                    viewModel.editTaskById(
                        taskId,
                        FormTask(
                            title = title,
                            description = description,
                            category = selectCategory,
                            date = date,
                            time = time
                        )
                    )
                } else {
                    viewModel.createTask(
                        FormTask(
                            title = title,
                            description = description,
                            category = selectCategory,
                            date = date,
                            time = time
                        )
                    )
                }
            }
        )
    }

    if(showTimePicker.value){
        TimePickerDialog(
            onConfirm = { hour, minute ->
                viewModel.setTime(hour, minute)
                showTimePicker.value = false
            },
            onDismiss = {
                showTimePicker.value = false
            }
        )
    }

    if (showDatePicker.value){
        DatePickerDialog(
            onConfirm = { year, month, day ->
                viewModel.setDate(year, month, day)
                showDatePicker.value = false
            },
            onDismiss = {
                showDatePicker.value = false
            }
        )
    }

    categoryToDelete.value?.let { category ->
        val isDeleting = deleteCategoryState is UiState.Loading
        ConfirmDialog(
            title = "Hapus Kategori",
            message = "Hapus kategori '${category.name}'?",
            confirmText = "Hapus",
            dismissText = "Batal",
            isConfirming = isDeleting,
            onDismiss = { categoryToDelete.value = null },
            onConfirm = {
                if (viewModel.selectCategory.value == category.name) {
                    viewModel.updateCategory("")
                }
                viewModel.deleteCategoryOptions(category.id)
            }
        )
    }

    if (showAddCategoryDialog.value) {
        val isCreating = categoryState is UiState.Loading
        FormDialog(
            title = "Tambah Kategori",
            confirmText = "Tambah",
            dismissText = "Batal",
            confirmEnabled = newCategoryName.value.isNotBlank(),
            isSubmitting = isCreating,
            onDismiss = {
                showAddCategoryDialog.value = false
                newCategoryName.value = ""
            },
            onConfirm = {
                val name = newCategoryName.value.trim()
                if (name.isNotBlank()) {
                    viewModel.createCategoryOptions(name)
                }
            },
            content = {
                OutlinedTextField(
                    value = newCategoryName.value,
                    onValueChange = { newCategoryName.value = it },
                    placeholder = { Text("Nama kategori") },
                    singleLine = true,
                    enabled = !isCreating
                )
            }
        )
    }




    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),

            ) {
            headerTask(
                titleHeader = if (taskId.isEmpty()) "Create Task" else "Edit Task",
                navController = navController
            )
            Spacer(modifier = Modifier.height(31.dp))
            TitleTextField("Title")
            CustomTextField(
                values = title,
                onValueChange = {
                    viewModel.updateTitle(it)
                },
                placeholder = "Title",
                icon = Icons.Filled.Edit,
                contentDescription = "Title Icon",
                keyboardType = KeyboardType.Text,
                isError = titleError != null,
                errorMessage = titleError
            )
            TitleTextField("Deadline")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 22.dp, start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = time ,
                    onValueChange = {},
                    placeholder = { Text("Time") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            showTimePicker.value = true
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
                        IconButton(onClick = { showDatePicker.value = true }) {
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

            if (timeError != null || dateError != null) {
                Text(
                    text = listOfNotNull(timeError, dateError).joinToString("\n"),
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                )
            }

            TitleTextField("Category")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 8.dp, bottom = 22.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                categories.forEach { category ->
                    ButtonCategory(
                        btnTitle = category.name,
                        onCategoryClick = { selected ->
                            viewModel.updateCategory(selected)
                        },
                        isSelected = selectCategory == category.name,
                        onLongClick = {
                            categoryToDelete.value = category
                        }
                    )
                }
                AddCategoryButton(
                    onClick = {
                        showAddCategoryDialog.value = true
                    }
                )
            }
            if (categoryError != null) {
                Text(
                    text = categoryError ?: "",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                )
            } else {
                Text(
                    text = "Tekan lama pada kategori untuk menghapusnya",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                )
            }
            TitleTextField("Description")
            OutlinedTextField(
                value = description,
                onValueChange = {
                    viewModel.updateDescription(it)
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
                    if (viewModel.validateForm()) {
                        showDialog.value = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = if (taskId.isNotEmpty()) "Update Task" else "Create Task",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        if (taskState is UiState.Loading) {
            LoadingContent()
        }
        if (taskId.isNotEmpty() && taskState is UiState.Error) {
            ErrorContent(
                message = (taskState as UiState.Error).errorMessage,
                onRetry = { viewModel.getTaskById(taskId) }
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

@Composable
fun AddCategoryButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(40.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        Text(
            text = "+",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Color.White,
        )
    }
}


@Preview(showBackground = true)
@Composable
fun TaskScreenPreview() {
    IngatinTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
//            TaskScreen(navController)
        }
    }
}