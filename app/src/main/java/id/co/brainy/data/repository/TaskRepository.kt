package id.co.brainy.data.repository

import android.util.Log
import id.co.brainy.data.model.TaskReq
import id.co.brainy.data.network.response.DeleteResponse
import id.co.brainy.data.network.response.TasksItem
import id.co.brainy.data.network.retrofit.ApiConfig
import id.co.brainy.data.network.retrofit.ApiService
import id.co.brainy.data.utils.UserPreferences
import id.co.brainy.data.utils.parseErrorMessage
import kotlinx.coroutines.flow.first

class TaskRepository(private val apiService: ApiService, private val userPreferences: UserPreferences) {

    suspend fun getAllTasks(): Result<List<TasksItem>?>{
        return try {
            val token = userPreferences.getToken().first()
            val userId = userPreferences.getUserId().first()

            if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
                return Result.failure(Exception("Token atau UserId kosong"))
            }

            val response = apiService.getAllTasks(ApiConfig.getAuthHeader(token), userId)
            Result.success(response.tasks)
        } catch (e: Exception){
            val message = parseErrorMessage(e)
            Result.failure(Exception(message))
        }
    }

    suspend fun getTaskByCategory(category: String): Result<List<TasksItem>?>{
        return try {
            val token = userPreferences.getToken().first()
            val userId = userPreferences.getUserId().first()

            if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
                return Result.failure(Exception("Token atau UserId kosong"))
            }

            val response = apiService.getTaskByCategory(ApiConfig.getAuthHeader(token),userId, category)
            Result.success(response.tasks)
        } catch (e: Exception){
            val message = parseErrorMessage(e)
            Result.failure(Exception(message))
        }
    }

    suspend fun getTaskById(taskId: String): Result<List<TasksItem>?>{
        return try {
            val token = userPreferences.getToken().first()

            val response = apiService.getTaskById(ApiConfig.getAuthHeader(token),taskId)
            Log.d("TaskRepository", "getTaskById: $response")
            Result.success(response.tasks)
        } catch (e: Exception){
            val message = parseErrorMessage(e)
            Result.failure(Exception(message))
        }
    }

    suspend fun createTask(
        category: String,
        dueDate: String,
        title: String,
        desc: String,
    ): Result<TasksItem>{
        return try {
            val token = userPreferences.getToken().first()
            val userId = userPreferences.getUserId().first()

            if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
                return Result.failure(Exception("Token atau UserId kosong"))
            }
            val response = apiService.createTask(
                ApiConfig.getAuthHeader(token),
                TaskReq(
                    category = category,
                    dueDate = dueDate,
                    title = title,
                    userId = userId,
                    desc = desc
                ))
            Result.success(response)
        }catch (e: Exception){
          val message = parseErrorMessage(e)
          Result.failure(Exception(message))
        }
    }

    suspend fun editTask(
        taskId: String,
        category: String,
        dueDate: String,
        title: String,
        desc: String
    ): Result<TasksItem> {
        return try {
            val token = userPreferences.getToken().first()
            val userId = userPreferences.getUserId().first()

            if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
                return Result.failure(Exception("Token atau UserId kosong"))
            }

            val request = TaskReq(
                category = category,
                dueDate = dueDate,
                title = title,
                userId = userId,
                desc = desc
            )

            val response = apiService.editTask(
                token = ApiConfig.getAuthHeader(token),
                taskId = taskId,
                request = request
            )

            Result.success(response)
        } catch (e: Exception) {
            val message = parseErrorMessage(e)
            Result.failure(Exception(message))
        }
    }

    suspend fun deleteTask(
        taskId: String
    ): Result<DeleteResponse> {
        return try {
            val token = userPreferences.getToken().first()
            Log.d("deleteTaskRepository", "getTaskById: $taskId")
            val response = apiService.deleteTask(
                ApiConfig.getAuthHeader(token),
                taskId
            )
            Log.d("deleteTaskRepository", "getTaskById: $response")
            Result.success(response)
        } catch (e: Exception){
            val message = parseErrorMessage(e)
            Result.failure(Exception(message))
        }
    }

}