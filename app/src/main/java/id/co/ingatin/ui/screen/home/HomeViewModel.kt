package id.co.ingatin.ui.screen.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.ingatin.data.model.CategoryCount
import id.co.ingatin.data.model.Task
import id.co.ingatin.data.model.toCategoryCount
import id.co.ingatin.data.model.toDomain
import id.co.ingatin.data.repository.TaskRepository
import id.co.ingatin.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _taskState = MutableStateFlow<UiState<List<Task>>>(UiState.Empty)
    val taskState = _taskState.asStateFlow()

    private val _countCategories = MutableStateFlow<UiState<CategoryCount>>(UiState.Empty)
    val countCategories = _countCategories.asStateFlow()



    init {
        refreshHome()
    }


    fun refreshHome() {
        _taskState.value = UiState.Loading
        _countCategories.value = UiState.Loading
        viewModelScope.launch {
            val response = taskRepository.getAllTasks()
            response.onSuccess { tasks ->
                _taskState.value = UiState.Success(tasks.map { it.toDomain() })
                _countCategories.value = UiState.Success(tasks.toCategoryCount())
            }.onFailure { error ->
                val message = error.message ?: "Unknown Error"
                _taskState.value = UiState.Error(message)
                _countCategories.value = UiState.Error(message)
            }
        }
    }

}