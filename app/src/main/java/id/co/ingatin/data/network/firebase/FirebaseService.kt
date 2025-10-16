package id.co.ingatin.data.network.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.co.ingatin.data.model.MyTask
import id.co.ingatin.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseService @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    //    AUTH
    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID not found")
            val user = firestore.collection("users")
                .document(userId)
                .get()
                .await()
                .toObject(User::class.java) ?: throw Exception("User not found")
            Log.d(HOME, "User: $user")
            Log.d(AUTH, "loginUserWithEmail:success")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(AUTH, "loginUserWithEmail:failure", e)
            Result.failure(e)
        }
    }

    suspend fun createAccount(email: String, password: String, nama: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uId = authResult.user?.uid ?: throw Exception("User ID not found")
            val user = User(
                uid = uId,
                name = nama,
                email = email,
            )
            firestore.collection("users")
                .document(uId)
                .set(user)
                .await()
            Log.d(AUTH, "createUserWithEmail:success")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(AUTH, "createUserWithEmail:failure", e)
            Result.failure(e)
        }
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun getCurrentUser(): Result<User>{
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("User ID not found")
            val user = firestore.collection("users")
                .document(uid)
                .get()
                .await()
                .toObject(User::class.java) ?: throw Exception("User not found")
            Log.d(HOME, "User: $user")
            Result.success(user)
        } catch (e: Exception){
            Log.e(HOME, "getCurrentUser:failure", e)
            Result.failure(e)
        }
    }

    //    Tasks
    suspend fun createTasks(title: String, category: String, description: String): Result<MyTask> {
        return try {
            val uId = auth.currentUser?.uid ?: throw Exception("User ID not found")
            val task = MyTask(
                title = title,
                category = category,
                description = description
            )
            firestore.collection("users").document(uId)
                .collection("tasks")
                .add(task)
                .await()
            Log.d(TASK, "createTasks:success")
            Result.success(task)
        } catch (e: Exception) {
            Log.e(TASK, "createTasks:failure")
            Result.failure(e)
        }
    }

    suspend fun getAllTasks(): Result<List<MyTask>> {
        return try {
            val uId = auth.currentUser?.uid ?: throw Exception("User ID not found")
            val tasks = firestore.collection("users").document(uId)
                .collection("tasks")
                .get()
                .await()
                .toObjects(MyTask::class.java) ?: throw Exception("Tasks not found")
            Log.d(TASK, "getTasks:success")
            Result.success(tasks)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun getTaskByCategory(category: String): Result<List<MyTask>> {
        return try {
            val uId = auth.currentUser?.uid ?: throw Exception("User ID not found")
            val tasks = firestore.collection("users").document(uId)
                .collection("tasks")
                .whereEqualTo("category", category)
                .get()
                .await()
                .toObjects(MyTask::class.java) ?: throw Exception("Tasks not found")
            Log.d(TASK, "getTaskByCategory:success")
            Result.success(tasks)
        }catch (e: Exception){
            Log.e(TASK, "getTaskByCategory:failure")
            Result.failure(e)
        }
    }

    suspend fun getTaskById(taskId: String): Result<MyTask>{
        return try {
            val uId = auth.currentUser?.uid ?: throw Exception("User ID not found")
            val task = firestore.collection("users").document(uId)
                .collection("tasks").document(taskId)
                .get()
                .await()
                .toObject(MyTask::class.java) ?: throw Exception("Task not found")
            Log.d(TASK, "getTaskById:success")
            Result.success(task)
        }catch (e: Exception){
            Log.e(TASK, "getTaskById:failure")
            Result.failure(e)
        }

    }


    companion object {
        private const val AUTH = "Authentication"
        private const val HOME = "Home"
        private const val TASK = "Task"
    }


}