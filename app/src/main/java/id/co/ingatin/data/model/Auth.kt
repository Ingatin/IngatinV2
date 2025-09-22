package id.co.ingatin.data.model

data class RegisterReq(
    val userName: String,
    val email: String,
    val password: String
)

data class LoginReq(
    val email: String,
    val password: String
)