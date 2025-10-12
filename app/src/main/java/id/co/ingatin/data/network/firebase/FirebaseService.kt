package id.co.ingatin.data.network.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import id.co.ingatin.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseService @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

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
                uId = uId,
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


    companion object {
        private const val AUTH = "Authentication"
        private const val HOME = "Home"
    }


}