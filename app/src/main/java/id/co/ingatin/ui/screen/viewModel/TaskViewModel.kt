package id.co.ingatin.ui.screen.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.ingatin.data.model.MyTask
import id.co.ingatin.data.repository.TaskRepository
import id.co.ingatin.ui.common.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    val title = MutableStateFlow("")
    val description = MutableStateFlow("")
    val selectCategory = MutableStateFlow("")

    val allCount = MutableStateFlow(0)
    val workCount = MutableStateFlow(0)
    val academyCount = MutableStateFlow(0)

    val categories = listOf("Work", "Academy")

    private val _createTaskState = MutableStateFlow<UiState<MyTask>>(UiState.Empty)
    val createTaskState = _createTaskState.asStateFlow()

    private val _myTasks = MutableStateFlow<UiState<List<MyTask>>>(UiState.Empty)
    val myTasks = _myTasks.asStateFlow()

    private val _taskDetail = MutableStateFlow<UiState<MyTask>>(UiState.Empty)
    val taskDetail = _taskDetail.asStateFlow()

    private val _deleteTask = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val deleteTask = _deleteTask.asStateFlow()

    private fun updateTaskCount(tasks: List<MyTask>) {
        allCount.value = tasks.size
        workCount.value = tasks.count { it.category.equals("Work", ignoreCase = true) }
        academyCount.value = tasks.count { it.category.equals("Academy", ignoreCase = true) }
    }

    fun createTask(
        title: String,
        category: String,
        desc: String
    ) {
        _createTaskState.value = UiState.Loading
        viewModelScope.launch {
            val response = taskRepository.createTasks(title, category, desc)
            response.onSuccess {
                _createTaskState.value = UiState.Success(it)
//                scheduleReminder(context, title,desc,dueDate)
            }.onFailure {
                _createTaskState.value = UiState.Error(it.message ?: "Unknown Error")
            }
        }
    }

    fun getTask() {
        _myTasks.value = UiState.Loading
        viewModelScope.launch {
            val response = taskRepository.getAllTasks()
            response.onSuccess {
                _myTasks.value = UiState.Success(it)
                updateTaskCount(it)
            }.onFailure {
                _myTasks.value = UiState.Error(it.message ?: "Unknown Error")
            }
        }
    }

    fun getTaskByCategory(category: String) {
        _myTasks.value = UiState.Loading
        viewModelScope.launch {
            val response = taskRepository.getTaskByCategory(category)
            response.onSuccess {
                _myTasks.value = UiState.Success(it)
            }.onFailure {
                _myTasks.value = UiState.Error(it.message ?: "Unknown Error")
            }
        }
    }

    fun getTaskById(taskId: String) {
        _taskDetail.value = UiState.Loading
        Log.d("TaskViewModel", "getTaskById: $taskId")
        viewModelScope.launch {
            delay(1000L)
            val response = taskRepository.getTaskById(taskId)
            response.onSuccess {
                _taskDetail.value = UiState.Success(it)
            }.onFailure {
                _taskDetail.value = UiState.Error(it.message ?: "Unknown Error")
            }
        }
    }

    fun editTaskById(taskId: String) {

    }

    fun deleteTaskById(taskId: String) {
        _deleteTask.value = UiState.Loading
        Log.d("TaskViewModel", "getTaskById: $taskId")
        viewModelScope.launch {
            delay(1000L)
            val response = taskRepository.deleteTaskById(taskId)
            response.onSuccess {
                _deleteTask.value = UiState.Success(it)
            }.onFailure {
                _deleteTask.value = UiState.Error(it.message ?: "Unknown Error")
            }
        }
    }


}