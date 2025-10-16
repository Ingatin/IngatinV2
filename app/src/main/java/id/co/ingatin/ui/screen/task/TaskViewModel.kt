package id.co.ingatin.ui.screen.task

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.ingatin.data.model.MyTask
import id.co.ingatin.data.repository.TaskRepository
import id.co.ingatin.ui.common.UiState
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

    private fun updateTaskCount(tasks: List<MyTask>) {
        allCount.value = tasks.size
        workCount.value = tasks.count{ it.category.equals("Work", ignoreCase = true)}
        academyCount.value = tasks.count{ it.category.equals("Academy", ignoreCase = true)}
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

    fun getMyTasks(category: String) {
        _myTasks.value = UiState.Loading
        viewModelScope.launch {
            val response = if (category == "All Task") {
                taskRepository.getAllTasks()
            } else {
                taskRepository.getTaskByCategory(category)
            }
            response.onSuccess {
                _myTasks.value = UiState.Success(it)
                updateTaskCount(it)
            }.onFailure {
                _myTasks.value = UiState.Error(it.message ?: "Unknown Error")
            }
        }
    }

    fun getTaskById(taskId: String) {
        _taskDetail.value = UiState.Loading
        viewModelScope.launch {
            val response = taskRepository.getTaskById(taskId)
            response.onSuccess {
                _taskDetail.value = UiState.Success(it)
            }.onFailure {
                _taskDetail.value = UiState.Error(it.message ?: "Unknown Error")
            }
        }
    }

}


//    private val _editTask = MutableStateFlow<UiState<TasksItem>>(UiState.Loading)
//    val editTask: MutableStateFlow<UiState<TasksItem>> = _editTask
//
//    private val _deleteTask = MutableStateFlow<UiState<DeleteResponse>>(UiState.Loading)
//    val deleteTask: MutableStateFlow<UiState<DeleteResponse>> = _deleteTask
//
//    private val _date = MutableLiveData("")
//    var date: LiveData<String> = _date
//
//    private val _time = MutableLiveData("")
//    var time: LiveData<String> = _time
//
//    private val _dateTime = MutableLiveData("")
//    var dateTime: LiveData<String> = _dateTime


//
//    fun selectDate(context: Context) {
//        val currentDate = Calendar.getInstance()
//        DatePickerDialog(
//            context, { _, year, month, day ->
//                val formatted = String.format(
//                    "%02d-%02d-%d",
//                    day,
//                    month + 1,
//                    year
//                )
//                _date.value = formatted
//                updateDateTime()
//            },
//            currentDate.get(Calendar.YEAR),
//            currentDate.get(Calendar.MONTH),
//            currentDate.get(Calendar.DAY_OF_MONTH)
//        ).show()
//    }
//
//    fun selectTime(context: Context) {
//        val calendar = Calendar.getInstance()
//        TimePickerDialog(
//            context,
//            { _, hour, minute ->
//                val formatted = String.format("%02d:%02d", hour, minute)
//                _time.value = formatted
//                updateDateTime()
//            },
//            calendar.get(Calendar.HOUR_OF_DAY),
//            calendar.get(Calendar.MINUTE),
//            true
//        ).show()
//    }
//
//    private fun updateDateTime() {
//        val currentDate = _date.value ?: ""
//        val currentTime = _time.value ?: ""
//
//        if (currentDate.isNotEmpty() && currentTime.isNotEmpty()) {
//            _dateTime.value = "$currentDate $currentTime"
//        }
//    }
//
//    fun deleteTask(taskId: String){
//        viewModelScope.launch {
//            val result = repository.deleteTask(taskId)
//            Log.d("resultDeleteviewmodel", "result: $result")
//            result
//                .onSuccess { _deleteTask.value = UiState.Success(it)}
//                .onFailure { _deleteTask.value = UiState.Error(it.message ?: "Unknown error") }
//        }
//    }
//
//    fun setDate(newDate: String) {
//        _date.value = newDate
//        updateDateTime()
//    }
//
//    fun setTime(newTime: String) {
//        _time.value = newTime
//        updateDateTime()
//    }
//
//    fun editTask(
//        context: Context,
//        taskId: String,
//        category: String,
//        dueDate: String,
//        title: String,
//        desc: String
//    ){
//        _editTask.value = UiState.Loading
//        viewModelScope.launch {
//            val response = repository.editTask(taskId, category, dueDate, title, desc)
//            response.onSuccess {
//                _editTask.value = UiState.Success(it)
//                scheduleReminder(context, title,desc, dueDate)
//            }.onFailure {
//                _editTask.value = UiState.Error(it.message ?: "Unknown Error")
//            }
//        }
//    }