package id.co.brainy.data.model

data class RegisterReq(
    val userName: String,
    val email: String,
    val password: String
)

data class LoginReq(
    val email: String,
    val password: String
)