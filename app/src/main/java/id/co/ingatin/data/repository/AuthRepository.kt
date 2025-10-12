package id.co.ingatin.data.repository

import id.co.ingatin.data.model.User
import id.co.ingatin.data.network.firebase.FirebaseService
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebase: FirebaseService
) {
    suspend fun regist(email: String, password: String, nama: String): Result<User> {
        return firebase.createAccount(email, password, nama)
    }

    suspend fun login(email: String, password: String): Result<User> {
        return firebase.loginUser(email, password)
    }

    fun logout() {
        firebase.logout()
    }

    fun isUserLoggedIn(): Boolean {
        return firebase.isUserLoggedIn()
    }
}