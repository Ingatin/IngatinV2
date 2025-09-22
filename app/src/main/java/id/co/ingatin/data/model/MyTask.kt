package id.co.ingatin.data.model

data class TaskReq(
    val category: String,
    val dueDate: String,
    val title: String,
    val userId: String,
    val desc: String
)
