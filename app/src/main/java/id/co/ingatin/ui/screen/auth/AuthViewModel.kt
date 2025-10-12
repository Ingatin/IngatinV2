package id.co.ingatin.ui.screen.auth

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val name = MutableStateFlow("")

    private val _registState = MutableStateFlow<UiState<User>>(UiState.Empty)
    val registState = _registState.asStateFlow()

    private val _loginState = MutableStateFlow<UiState<User>>(UiState.Empty)
    val loginState = _loginState.asStateFlow()

    private val _logoutState = MutableStateFlow(false)
    val logoutState = _logoutState.asStateFlow()

    fun register(username: String, email: String, password: String) {
        _registState.value = UiState.Loading
        viewModelScope.launch {
            val response = authRepository.regist(email, password, username)
            response.onSuccess {
                _registState.value = UiState.Success(it)
            }.onFailure {
                _registState.value = UiState.Error(it.message ?: "Unknown Error")
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
}