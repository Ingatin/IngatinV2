package id.co.ingatin.ui.screen.task

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.ingatin.data.model.Category
import id.co.ingatin.data.model.FormTask
import id.co.ingatin.data.model.Task
import id.co.ingatin.data.model.toCategory
import id.co.ingatin.data.model.toDomain
import id.co.ingatin.data.repository.TaskRepository
import id.co.ingatin.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {


    private val _createTaskState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val createTaskState = _createTaskState.asStateFlow()

    private val _updateTaskState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val updateTaskState = _updateTaskState.asStateFlow()

    private val _deleteTask = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val deleteTask = _deleteTask.asStateFlow()

    private val _taskById = MutableStateFlow<UiState<Task>>(UiState.Empty)
    val taskById = _taskById.asStateFlow()

    private val _taskState = MutableStateFlow<UiState<List<Task>>>(UiState.Empty)
    val taskState = _taskState.asStateFlow()

    private val _categoryOption = MutableStateFlow<UiState<List<Category>>>(UiState.Empty)
    val categoryOption = _categoryOption.asStateFlow()

    private val _categoryState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val categoryState = _categoryState.asStateFlow()

    private val _deleteCategory = MutableStateFlow<UiState<String>>(UiState.Empty)
    val deleteCategory = _deleteCategory.asStateFlow()


    val title = MutableStateFlow("")
    val description = MutableStateFlow("")
    val selectCategory = MutableStateFlow("")
    val selectDate = MutableStateFlow("")
    val selectTime = MutableStateFlow("")

    val titleError = MutableStateFlow<String?>(null)
    val categoryError = MutableStateFlow<String?>(null)
    val dateError = MutableStateFlow<String?>(null)
    val timeError = MutableStateFlow<String?>(null)

    private val _selectedCategory = MutableStateFlow("All")

    fun getCategoryNames(): List<String> =
        when (val state = _categoryOption.value) {
            is UiState.Success -> state.data.map { it.name }
            else -> emptyList()
        }


    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateTitle(value: String) {
        title.value = value
        titleError.value = null
    }

    fun updateDescription(value: String) {
        description.value = value
    }

    fun updateCategory(category: String) {
        selectCategory.value = category
        categoryError.value = null
    }

    fun validateForm(): Boolean {
        var isValid = true

        if (title.value.isBlank()) {
            titleError.value = "Title wajib diisi"
            isValid = false
        } else if (title.value.length < 3) {
            titleError.value = "Title minimal 3 karakter"
            isValid = false
        }

        if (selectCategory.value.isBlank()) {
            categoryError.value = "Pilih kategori terlebih dahulu"
            isValid = false
        }

        if (selectDate.value.isBlank()) {
            dateError.value = "Pilih tanggal deadline"
            isValid = false
        }

        if (selectTime.value.isBlank()) {
            timeError.value = "Pilih waktu deadline"
            isValid = false
        }

        return isValid
    }

    fun getTaskById(taskId: String) {
        if (taskId.isEmpty()) return

        _taskById.value = UiState.Loading
        viewModelScope.launch {
            val response = taskRepository.getTaskById(taskId)
            response.onSuccess {
                val data = it.toDomain()
                _taskById.value = UiState.Success(data)
                title.value = data.title
                description.value = data.description
                selectCategory.value = data.category
                selectDate.value = data.date
                selectTime.value = data.time
            }.onFailure {
                _taskById.value = UiState.Error(it.message ?: "Unknow Error")
            }
        }


    }

    fun createTask(
        form: FormTask
    ) {
        if (!validateForm()) return
        Log.d("TaskViewModel", "createTask: $form")
        _createTaskState.value = UiState.Loading
        viewModelScope.launch {
            val response = taskRepository.createTasks(form)

            response.onSuccess {
                _createTaskState.value =
                    UiState.Success("Berhasil membuat task ${form.category} baru")
            }.onFailure {
                _createTaskState.value = UiState.Error(it.message ?: "Unknow Error")
            }
        }
    }

    fun getAllTasks() {
        _taskState.value = UiState.Loading
        viewModelScope.launch {
            val response = taskRepository.getAllTasks()
            response.onSuccess {
                val domainList = it.map { it.toDomain() }
                _taskState.value = UiState.Success(domainList)
            }.onFailure {
                _taskState.value = UiState.Error(it.message ?: "Unknow Error")
            }
        }
    }

    fun editTaskById(taskId: String, form: FormTask) {
        if (!validateForm()) return
        _updateTaskState.value = UiState.Loading
        viewModelScope.launch {
            val response = taskRepository.editTaskById(taskId, form)
            response.onSuccess {
                _updateTaskState.value = UiState.Success(it)
            }.onFailure {
                _updateTaskState.value = UiState.Error(it.message ?: "Unknown Error")
            }
        }
    }

    fun deleteTaskById(taskId: String) {
        _deleteTask.value = UiState.Loading
        viewModelScope.launch {
            val response = taskRepository.deleteTaskById(taskId)
            response.onSuccess {
                _deleteTask.value = UiState.Success(it)
            }.onFailure {
                _deleteTask.value = UiState.Error(it.message ?: "Unknown Error")
            }
        }
    }

    fun getCategoryOptions() {
        _categoryOption.value = UiState.Loading
        viewModelScope.launch {
            val response = taskRepository.getAllCategories()
            response.onSuccess {
                val response = it.map { it.toCategory() }
                _categoryOption.value = UiState.Success(response)
            }.onFailure {
                _categoryOption.value = UiState.Error(it.message ?: "Unknow Error")
            }
        }
    }

    fun createCategoryOptions(name: String) {
        _categoryState.value = UiState.Loading
        viewModelScope.launch {
            val response = taskRepository.createCategory(name)
            response.onSuccess {
                _categoryState.value = UiState.Success("Berhasil menambah kategori baru")
            }.onFailure {
                _categoryState.value = UiState.Error(it.message ?: "Unknow Error")
            }
        }
    }

    fun deleteCategoryOptions(id: String) {
        _deleteCategory.value = UiState.Loading
        viewModelScope.launch {
            val response = taskRepository.deleteCategoryById(id)
            response.onSuccess {
                _deleteCategory.value = UiState.Success("Berhasil menghapus kategori")
            }.onFailure {
                _deleteCategory.value = UiState.Error(it.message ?: "Unknow Error")
            }
        }
    }

    fun setDate(year: Int, month: Int, day: Int) {
        selectDate.value = "%02d-%02d-%d".format(day, month, year)
        dateError.value = null
        Log.d("TaskViewModel", "setDate: ${selectDate.value}")
    }

    fun setTime(hour: Int, minute: Int) {
        selectTime.value = "%02d:%02d".format(hour, minute)
        timeError.value = null
        Log.d("TaskViewModel", "setTime: ${selectTime.value}")
    }
}