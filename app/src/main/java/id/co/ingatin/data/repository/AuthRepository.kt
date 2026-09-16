package id.co.ingatin.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.co.ingatin.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun register(email: String, password: String, nama: String): Result<String> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val id = authResult.user?.uid ?: throw Exception("User ID not found")
            val user = User(
                uid = id,
                name = nama,
                email = email,
            )
            firestore.collection("users")
                .document(id)
                .set(user)
                .await()
            Log.d(AUTH, "createUserWithEmail:success")
            Result.success("Berhasil membuat akun baru")
        } catch (e: Exception) {
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

    suspend fun logout() {
        auth.signOut()
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