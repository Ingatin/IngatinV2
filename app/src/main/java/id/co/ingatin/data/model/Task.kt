package id.co.ingatin.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.sql.Time
import java.util.Date

data class TaskDto(
    @DocumentId
    val taskId: String = "",
    val category: String,
    val dueDate: Timestamp,
    val title: String,
    val description: String = ""
)

data class FormTask(
    val title: String,
    val description: String = "",
    val category: String,
    val date: String,
    val time: String
)

data class Task(
    val id: String,
    val title: String,
    val description: String = "",
    val category: String,
    val date: String,
    val time: String
)

