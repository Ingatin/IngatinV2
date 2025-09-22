package id.co.brainy.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.co.brainy.data.network.response.LoginResponse
import id.co.brainy.data.network.response.RegistResponse
import id.co.brainy.data.repository.AuthRepository
import id.co.brainy.ui.common.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _registerState = MutableStateFlow<UiState<RegistResponse>>(UiState.Empty)
    val registerState: StateFlow<UiState<RegistResponse>> = _registerState

    private val _loginState = MutableStateFlow<UiState<LoginResponse>>(UiState.Empty)
    val loginState: StateFlow<UiState<LoginResponse>> = _loginState

    private val _logoutState = MutableStateFlow(false)
    val logoutState: StateFlow<Boolean> = _logoutState

    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username

    fun register(username: String, email: String, password: String) {
        _registerState.value = UiState.Loading
        viewModelScope.launch {
            val response = authRepository.register(username, email, password)
            response.onSuccess {
                _registerState.value = UiState.Success(it)
            }.onFailure {
                _registerState.value = UiState.Error(it.message ?: "Unknown Error")
            }

        }
    }

    fun login(email: String, password: String) {
        _loginState.value = UiState.Loading
        viewModelScope.launch {
            val response = authRepository.login(email, password)
            response.onSuccess {
                _loginState.value = UiState.Success(it)
            }.onFailure {
                _loginState.value = UiState.Error(it.message ?: "Unknown Error")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
                _logoutState.value = true
                delay(1000)
                _logoutState.value = false
            } catch (e: Exception) {
                _logoutState.value = false
            }
        }
    }

    fun getUser() {
        viewModelScope.launch {
            val response = authRepository.getUser()
            response.onSuccess {
                _username.value = it.username
            }.onFailure {
                _username.value = null
            }
        }

    }
}