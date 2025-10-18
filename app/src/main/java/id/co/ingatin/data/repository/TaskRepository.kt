package id.co.ingatin.data.repository

import android.util.Log
import id.co.ingatin.data.model.MyTask
import id.co.ingatin.data.network.firebase.FirebaseService
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val firebase: FirebaseService
) {
    suspend fun createTasks(title: String, category: String, description: String): Result<MyTask> {
        return firebase.createTasks(title, category, description)
    }

    suspend fun getAllTasks(): Result<List<MyTask>> {
        return firebase.getAllTasks()
    }

    suspend fun getTaskByCategory(category: String): Result<List<MyTask>> {
        return firebase.getTaskByCategory(category)
    }

    suspend fun getTaskById(taskId: String): Result<MyTask> {
        return firebase.getTaskById(taskId)
    }

    suspend fun editTaskById(taskId: String, updateTask: MyTask): Result<Boolean> {
        return try {
            firebase.editTaskById(taskId, updateTask)
            Log.d(TASK, "editTaskById success: taskId = $taskId & updateTask = $updateTask")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TASK, "editTaskById failure: taskId = $taskId")
            Result.failure(e)
        }
    }

    suspend fun deleteTaskById(taskId: String): Result<Boolean> {
        return try {
            firebase.deleteTaskById(taskId)
            Log.d(TASK, "deleteTaskById success: taskId = $taskId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TASK, "deleteTaskById failure: taskId = $taskId")
            Result.failure(e)
        }
    }


    companion object {
        private const val TASK = "TaskRepository"
    }

}