package id.co.ingatin.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.co.ingatin.data.model.CategoryDto
import id.co.ingatin.data.model.FormTask
import id.co.ingatin.data.model.TaskDto
import id.co.ingatin.data.utils.toTimestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createTasks(form: FormTask): Result<String> {
        return try {
            val dueDate = toTimestamp(form.date, form.time)
            val task = TaskDto(
                title = form.title,
                dueDate = dueDate,
                category = form.category,
                description = form.description
            )
            val uid = auth.currentUser?.uid ?: throw Exception("User ID not found")
            firestore.collection("users").document(uid)
                .collection("tasks")
                .add(task)
                .await()
            Log.d(TASK, "createTasks success: $task")
            Result.success("Berhasil membuat tugas baru")
        } catch (e: Exception) {
            Log.e(TASK, "createTasks failure", e)
            Result.failure(e)
        }
    }

    suspend fun getAllTasks(): Result<List<TaskDto>> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("User ID not found")
            val task = firestore.collection("users").document(uid)
                .collection("tasks")
                .get()
                .await()
                .toObjects(TaskDto::class.java)
            Log.d(TASK, "getAllTasks:success")
            Result.success(task)
        } catch (e: Exception) {
            Log.e(TASK, "getAllTasks:failure", e)
            Result.failure(e)
        }
    }

    private suspend fun generateIdCategory(uid: String): String {
        val count = firestore.collection("users").document(uid)
            .collection("categories")
            .get()
            .await()
            .size()
        return "c-%03d".format(count + 1)
    }

    suspend fun createCategory(name: String): Result<String> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("User ID not found")
            val id = generateIdCategory(uid)
            val data = hashMapOf("name" to name)
            firestore.collection("users").document(uid)
                .collection("categories").document(id)
                .set(data)
                .await()
            Log.d(TASK, "createCategory success: $id")
            Result.success(id)
        } catch (e: Exception) {
            Log.e(TASK, "createCategory failure", e)
            Result.failure(e)
        }
    }

    suspend fun getAllCategories(): Result<List<CategoryDto>> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("User ID not found")
            val categories = firestore.collection("users").document(uid)
                .collection("categories")
                .get()
                .await()
                .toObjects(CategoryDto::class.java)
            Result.success(categories)
        } catch (e: Exception) {
            Log.e(TASK, "getAllCategories failure", e)
            Result.failure(e)
        }
    }

    suspend fun deleteCategoryById(categoryId: String): Result<Boolean> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("User ID not found")
            firestore.collection("users").document(uid)
                .collection("categories").document(categoryId)
                .delete()
                .await()
            Log.d(TASK, "deleteCategoryById success: $categoryId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TASK, "deleteCategoryById failure", e)
            Result.failure(e)
        }
    }

    suspend fun getTaskById(taskId: String): Result<TaskDto> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("User ID not found")
            val task = firestore.collection("users").document(uid)
                .collection("tasks").document(taskId)
                .get()
                .await()
                .toObject(TaskDto::class.java) ?: throw Exception("Task not found")
            Log.d(TASK, "getTaskById success: taskId = $taskId")
            Result.success(task)
        } catch (e: Exception) {
            Log.e(TASK, "getTaskById failure: taskId = $taskId", e)
            Result.failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun editTaskById(taskId: String, form: FormTask): Result<Boolean> {
        return try {
            val dueDate = toTimestamp(form.date, form.time)
            val updateTask = TaskDto(
                title = form.title,
                dueDate = dueDate,
                category = form.category,
                description = form.description
            )
            val uid = auth.currentUser?.uid ?: throw Exception("User ID not found")
            firestore.collection("users").document(uid)
                .collection("tasks").document(taskId)
                .set(updateTask.copy(taskId = taskId))
                .await()
            Log.d(TASK, "editTaskById success: taskId = $taskId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TASK, "editTaskById failure: taskId = $taskId", e)
            Result.failure(e)
        }
    }

    suspend fun deleteTaskById(taskId: String): Result<Boolean> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("User ID not found")
            firestore.collection("users").document(uid)
                .collection("tasks").document(taskId)
                .delete()
                .await()
            Log.d(TASK, "deleteTaskById success: taskId = $taskId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TASK, "deleteTaskById failure: taskId = $taskId", e)
            Result.failure(e)
        }
    }

    companion object {
        private const val TASK = "TaskRepository"
    }
}