package id.co.brainy.data.network.response

import com.google.gson.annotations.SerializedName

data class TaskResponse(

    @field:SerializedName("tasks")
    val tasks: List<TasksItem>? = null
)

data class TasksItem(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("userId")
    val userId: String,

    @field:SerializedName("taskId")
    val taskId: String,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("category")
    val category: String,

    @field:SerializedName("dueDate")
    val dueDate: String,

    @field:SerializedName("desc")
    val desc: String,

    @field:SerializedName("createdAt")
    val createdAt: String,

)

data class DeleteResponse(

    @field:SerializedName("message")
    val message: String,

)