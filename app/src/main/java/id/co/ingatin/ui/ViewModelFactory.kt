package id.co.ingatin.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.co.ingatin.SplashViewModel
import id.co.ingatin.data.network.retrofit.ApiConfig
import id.co.ingatin.data.repository.AuthRepository
import id.co.ingatin.data.repository.TaskRepository
import id.co.ingatin.data.utils.UserPreferences
import id.co.ingatin.data.utils.dataStore
import id.co.ingatin.ui.screen.auth.AuthViewModel
import id.co.ingatin.ui.screen.home.HomeViewModel
import id.co.ingatin.ui.screen.task.TaskViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val apiService = ApiConfig.getApiService()
        val userPreferences = UserPreferences.getInstance(context.dataStore)
        val authRepository = AuthRepository(apiService, userPreferences)
        val taskRepository = TaskRepository(apiService, userPreferences)

        return when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(taskRepository) as T
            }
            modelClass.isAssignableFrom(TaskViewModel::class.java) -> {
                TaskViewModel(taskRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown viewmodel class: " + modelClass.name)
        }


    }

}