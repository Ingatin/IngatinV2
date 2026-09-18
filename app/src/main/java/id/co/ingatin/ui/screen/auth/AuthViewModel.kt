package id.co.ingatin.ui.screen.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.ingatin.data.model.User
import id.co.ingatin.data.repository.AuthRepository
import id.co.ingatin.ui.common.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val name = MutableStateFlow("")

    val emailError = MutableStateFlow<String?>(null)
    val passwordError = MutableStateFlow<String?>(null)
    val nameError = MutableStateFlow<String?>(null)

    private val _getUser = MutableStateFlow<UiState<User>>(UiState.Empty)
    val getUser = _getUser.asStateFlow()

    private val _registState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val registState = _registState.asStateFlow()

    private val _loginState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val loginState = _loginState.asStateFlow()

    private val _logoutState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val logoutState = _logoutState.asStateFlow()

    fun updateEmail(value: String) {
        email.value = value
        emailError.value = null
    }

    fun updatePassword(value: String) {
        password.value = value
        passwordError.value = null
    }

    fun updateName(value: String) {
        name.value = value
        nameError.value = null
    }

    fun register(name: String, email: String, password: String) {
        var isValid = true

        if (name.isBlank()) {
            nameError.value = "Nama tidak boleh kosong"
            isValid = false
        } else if (name.length < 3) {
            nameError.value = "Nama minimal 3 karakter"
            isValid = false
        }

        if (email.isBlank()) {
            emailError.value = "Email tidak boleh kosong"
            isValid = false
        } else if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError.value = "Format email tidak valid"
            isValid = false
        }

        if (password.isBlank()) {
            passwordError.value = "Password tidak boleh kosong"
            isValid = false
        } else if (password.length < 8 || !password.any { it.isDigit() }) {
            passwordError.value = "Password minimal 8 karakter dan harus mengandung angka"
            isValid = false
        }

        if (!isValid) return

        _registState.value = UiState.Loading
        viewModelScope.launch {
            val response = authRepository.register(email, password, name)
            response.onSuccess {
                _registState.value = UiState.Success("Daftar akun berhasil")
            }.onFailure {
                _registState.value = UiState.Error(it.message ?: "Unknown Error")
            }
        }
    }

    fun login(email: String, password: String) {
        var isValid = true

        if (email.isBlank()) {
            emailError.value = "Email tidak boleh kosong"
            isValid = false
        } else if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError.value = "Format email tidak valid"
            isValid = false
        }

        if (password.isBlank()) {
            passwordError.value = "Password tidak boleh kosong"
            isValid = false
        }

        if (!isValid) return

        _loginState.value = UiState.Loading
        viewModelScope.launch {
            val response = authRepository.login(email, password)
            response.onSuccess {
                _loginState.value = UiState.Success("Login Berhasil")
            }.onFailure {
                _loginState.value = UiState.Error(it.message ?: "Unknown Error")
            }
        }
    }

    fun logout() {
        _logoutState.value = UiState.Loading
        viewModelScope.launch {
            delay(2000L.milliseconds)
            val response = authRepository.logout()
            response.onSuccess {
                _logoutState.value = UiState.Success("Berhasil keluar dari akun")
            }.onFailure {
                _logoutState.value = UiState.Error(it.message ?: "Unknown Error")
            }
        }
    }

    fun getUser() {
        _getUser.value = UiState.Loading
        viewModelScope.launch {
            val result = authRepository.getCurrentUser()
            result.onSuccess {
                _getUser.value = UiState.Success(it)
            }.onFailure {
                _getUser.value = UiState.Error(it.message ?: "Unknown Error")
            }
        }
    }
}