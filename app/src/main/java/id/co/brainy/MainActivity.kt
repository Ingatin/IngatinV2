package id.co.brainy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import id.co.brainy.ui.ViewModelFactory
import id.co.brainy.ui.theme.BrainyTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        val factory = ViewModelFactory(applicationContext)
        viewModel = ViewModelProvider(this, factory)[SplashViewModel::class.java]


        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoading.value
        }

        setContent {
            BrainyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isLoading by viewModel.isLoading.collectAsState()
                    val startDestination by viewModel.startDestination.collectAsState()

                    if (!isLoading) {
                        BrainyApp(startDestination)
                    }
                }
            }
        }
    }
}
