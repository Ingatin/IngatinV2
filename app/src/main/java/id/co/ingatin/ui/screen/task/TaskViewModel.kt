package id.co.ingatin.ui.screen.task


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