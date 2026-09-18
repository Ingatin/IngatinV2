package id.co.ingatin.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import id.co.ingatin.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun register(email: String, password: String, name: String): Result<String> {
        var createdUser: FirebaseUser? = null
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            createdUser = authResult.user ?: throw Exception("User ID not found")
            val id = createdUser.uid
            val user = User(
                uid = id,
                name = name,
                email = email,
            )
            firestore.collection("users")
                .document(id)
                .set(user)
                .await()
            Log.d(AUTH, "createUserWithEmail:success")
            Result.success("Berhasil membuat akun baru")
        } catch (e: Exception) {
            if (createdUser != null) {
                try {
                    createdUser.delete().await()
                    Log.d(AUTH, "rollback: akun dihapus karena gagal simpan ke Firestore")
                } catch (rollbackEx: Exception) {
                    Log.e(AUTH, "rollback: gagal menghapus akun", rollbackEx)
                }
            }
            Log.e(AUTH, "createUserWithEmail:failure", e)
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User ID not found")
            val user = firestore.collection("users")
                .document(userId)
                .get()
                .await()
                .toObject(User::class.java) ?: throw Exception("User not found")
            Log.d(AUTH, "loginUserWithEmail:success")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(AUTH, "loginUserWithEmail:failure", e)
            Result.failure(e)
        }
    }

    fun logout(): Result<Boolean> {
        return try {
            auth.signOut()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    suspend fun getCurrentUser(): Result<User> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("User ID not found")
            val user = firestore.collection("users")
                .document(uid)
                .get()
                .await()
                .toObject(User::class.java) ?: throw Exception("User not found")
            Log.d(AUTH, "User: $user")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(AUTH, "getCurrentUser:failure", e)
            Result.failure(e)
        }
    }

    companion object {
        private const val AUTH = "AuthRepository"
    }
}