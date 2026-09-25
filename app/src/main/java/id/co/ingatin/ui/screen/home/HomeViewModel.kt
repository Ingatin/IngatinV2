package id.co.ingatin.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.ingatin.data.model.CategoryCount
import id.co.ingatin.data.model.Task
import id.co.ingatin.data.model.toCategoryCount
import id.co.ingatin.data.model.toDomain
import id.co.ingatin.data.repository.TaskRepository
import id.co.ingatin.data.utils.ConnectivityObserver
import id.co.ingatin.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _taskState = MutableStateFlow<UiState<List<Task>>>(UiState.Empty)
    val taskState = _taskState.asStateFlow()

    private val _countCategories = MutableStateFlow<UiState<CategoryCount>>(UiState.Empty)
    val countCategories = _countCategories.asStateFlow()

    init {
        viewModelScope.launch {
            connectivityObserver.isOnline.collect { online ->
                // Muat ulang otomatis saat koneksi pulih setelah sebelumnya gagal.
                if (online && _taskState.value is UiState.Error) {
                    refreshHome()
                }
            }
        }
    }

    fun refreshHome() {
        viewModelScope.launch {
            if (!connectivityObserver.isOnline.first()) {
                _taskState.value = UiState.Error(
                    "Tidak ada koneksi internet. Periksa kembali koneksi Anda."
                )
                _countCategories.value = UiState.Empty
                return@launch
            }
            _taskState.value = UiState.Loading
            _countCategories.value = UiState.Loading
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