package id.co.ingatin.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class MyTask(
    @DocumentId
    val taskId: String = "",
    val category: String = "",
    val dueDate: Timestamp,
    val title: String = "",
    val description: String = ""
)

