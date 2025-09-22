package id.co.brainy.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.co.brainy.data.network.response.TasksItem
import id.co.brainy.data.repository.TaskRepository
import id.co.brainy.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _taskAll = MutableStateFlow<UiState<List<TasksItem>?>>(UiState.Loading)
    val taskAll: StateFlow<UiState<List<TasksItem>?>> = _taskAll

    private val _taskCategory = MutableStateFlow<UiState<List<TasksItem>?>>(UiState.Loading)
    val taskCategory: StateFlow<UiState<List<TasksItem>?>> = _taskCategory





    fun getAllTasks() {
        viewModelScope.launch {
            _taskAll.value = UiState.Loading
            val response = repository.getAllTasks()
            response.onSuccess {
                _taskAll.value = UiState.Success(it ?: emptyList())
            }.onFailure {
                _taskAll.value = UiState.Error(it.message ?: "Unknown Error")
            }
        }
    }

    fun getTasksByCategory(category: String) {
        viewModelScope.launch {
            _taskCategory.value = UiState.Loading
            val result = when(category) {
                "Academy" -> repository.getTaskByCategory(category)
                "Work" -> repository.getTaskByCategory(category)
                else -> repository.getAllTasks()
            }
            result.onSuccess {
                _taskCategory.value = UiState.Success(it ?: emptyList())
                Log.d("HomeViewModel", "getTasksByCategory success: $category")
            }.onFailure {
                _taskCategory.value = UiState.Error(it.message ?: "Unknown Error")
                Log.d("HomeViewModel", "getTasksByCategory error: $category")
            }
        }
    }




}