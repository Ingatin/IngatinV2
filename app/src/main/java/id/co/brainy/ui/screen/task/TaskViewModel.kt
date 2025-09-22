package id.co.brainy.ui.screen.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.co.brainy.data.network.response.DeleteResponse
import id.co.brainy.data.network.response.TasksItem
import id.co.brainy.data.repository.TaskRepository
import id.co.brainy.ui.common.UiState
import id.co.brainy.ui.screen.notif.scheduleReminder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _createTask = MutableStateFlow<UiState<TasksItem>>(UiState.Loading)
    val createTask: MutableStateFlow<UiState<TasksItem>> = _createTask

    private val _editTask = MutableStateFlow<UiState<TasksItem>>(UiState.Loading)
    val editTask: MutableStateFlow<UiState<TasksItem>> = _editTask

    private val _taskDetail = MutableStateFlow<UiState<List<TasksItem>?>>(UiState.Loading)
    val taskDetail: StateFlow<UiState<List<TasksItem>?>> = _taskDetail

    private val _deleteTask = MutableStateFlow<UiState<DeleteResponse>>(UiState.Loading)
    val deleteTask: MutableStateFlow<UiState<DeleteResponse>> = _deleteTask

    private val _date = MutableLiveData("")
    var date: LiveData<String> = _date

    private val _time = MutableLiveData("")
    var time: LiveData<String> = _time

    private val _dateTime = MutableLiveData("")
    var dateTime: LiveData<String> = _dateTime

    fun selectDate(context: Context) {
        val currentDate = Calendar.getInstance()
        DatePickerDialog(
            context, { _, year, month, day ->
                val formatted = String.format(
                    "%02d-%02d-%d",
                    day,
                    month + 1,
                    year
                )
                _date.value = formatted
                updateDateTime()
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun selectTime(context: Context) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour, minute ->
                val formatted = String.format("%02d:%02d", hour, minute)
                _time.value = formatted
                updateDateTime()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun updateDateTime() {
        val currentDate = _date.value ?: ""
        val currentTime = _time.value ?: ""

        if (currentDate.isNotEmpty() && currentTime.isNotEmpty()) {
            _dateTime.value = "$currentDate $currentTime"
        }
    }

    fun createTask(
        context: Context,
        category: String,
        dueDate: String,
        title: String,
        desc: String
    ) {
        _createTask.value = UiState.Loading
        viewModelScope.launch {
            val response = repository.createTask(category, dueDate, title, desc)
            response.onSuccess {
                _createTask.value = UiState.Success(it)
                scheduleReminder(context, title,desc,dueDate)
            }.onFailure {
                _createTask.value = UiState.Error(it.message ?: "Unknown Error")
            }
        }
    }

    fun getTaskById(taskId: String) {
        viewModelScope.launch {
            val result = repository.getTaskById(taskId)
            Log.d("resultViemodelHome", "result: $result")
            result
                .onSuccess { _taskDetail.value = UiState.Success(it ?: emptyList()) }
                .onFailure { _taskDetail.value = UiState.Error(it.message ?: "Unknown error") }
        }
    }

    fun deleteTask(taskId: String){
        viewModelScope.launch {
            val result = repository.deleteTask(taskId)
            Log.d("resultDeleteviewmodel", "result: $result")
            result
                .onSuccess { _deleteTask.value = UiState.Success(it)}
                .onFailure { _deleteTask.value = UiState.Error(it.message ?: "Unknown error") }
        }
    }

    fun setDate(newDate: String) {
        _date.value = newDate
        updateDateTime()
    }

    fun setTime(newTime: String) {
        _time.value = newTime
        updateDateTime()
    }

    fun editTask(
        context: Context,
        taskId: String,
        category: String,
        dueDate: String,
        title: String,
        desc: String
    ){
        _editTask.value = UiState.Loading
        viewModelScope.launch {
            val response = repository.editTask(taskId, category, dueDate, title, desc)
            response.onSuccess {
                _editTask.value = UiState.Success(it)
                scheduleReminder(context, title,desc, dueDate)
            }.onFailure {
                _editTask.value = UiState.Error(it.message ?: "Unknown Error")
            }
        }
    }


}