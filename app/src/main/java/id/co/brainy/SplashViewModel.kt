package id.co.brainy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.co.brainy.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(private val authRepository: AuthRepository): ViewModel() {

    private val  _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _startDestination = MutableStateFlow("login")
    val startDestination = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            val token = authRepository.getToken().first()
            _startDestination.value = if (!token.isNullOrEmpty()) "home" else "login"
            _isLoading.value = false
        }
    }

}