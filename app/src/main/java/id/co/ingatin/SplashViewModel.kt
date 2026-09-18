package id.co.ingatin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.ingatin.data.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    private val  _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _startDestination = MutableStateFlow("login")
    val startDestination = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1500) // Delay hanya untuk efek animasi splash
            checkUserSession()
        }
    }

    private fun checkUserSession() {
        if (authRepository.isUserLoggedIn()) {
            // 🔹 Kalau user sudah login → langsung ke Home
            _startDestination.value = "home"
        } else {
            // 🔹 Kalau belum login → ke Login
            _startDestination.value = "login"
        }
        _isLoading.value = false
    }

}