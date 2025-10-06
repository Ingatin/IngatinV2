package id.co.ingatin.data.network.firebase

import android.util.Log
import id.co.ingatin.data.model.User
import io.grpc.perfmark.PerfMark.task
import kotlinx.coroutines.tasks.await

class FirebaseService {

    private val auth = FirebaseConfig.auth
    private val firestore = FirebaseConfig.firestore

    suspend fun createAccount(email: String, password: String, nama: String): Result<User> {
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uId = authResult.user?.uid ?: throw Exception("User ID not found")
            val user = User(
                name = nama,
                email = email,
            )
            firestore.collection("users")
                .document(uId)
                .set(user)
                .await()
            Log.d(AUTH, "createUserWithEmail:success")
            return Result.success(user)
        } catch (e: Exception) {
            Log.e(AUTH, "createUserWithEmail:failure", e)
            return Result.failure(e)
        }
    }

    companion object {
        private const val AUTH = "Authentication"
    }


}