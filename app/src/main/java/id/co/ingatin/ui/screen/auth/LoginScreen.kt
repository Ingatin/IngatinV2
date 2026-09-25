package id.co.ingatin.ui.screen.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.co.ingatin.Routes
import id.co.ingatin.ui.common.UiState
import id.co.ingatin.ui.components.CustomTextField
import id.co.ingatin.ui.components.LoadingContent
import id.co.ingatin.ui.theme.IngatinTheme

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {

    val context = LocalContext.current


    val loginState by viewModel.loginState.collectAsState()

    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()

    val isLoading = loginState is UiState.Loading

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is UiState.Success -> {
                val data = state.data
                Toast.makeText(context, data, Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }

            is UiState.Error -> {
                val errorMessage = state.errorMessage
                Toast.makeText(context, "Gagal Login: $errorMessage", Toast.LENGTH_SHORT).show()
            }

            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Sign In",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 32.sp, fontWeight = FontWeight.ExtraBold
                ),
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "enter your email and password",
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(modifier = Modifier.height(40.dp))
            CustomTextField(
                values = email,
                onValueChange = {
                    viewModel.updateEmail(it)
                },
                placeholder = "Email",
                icon = Icons.Default.Email,
                contentDescription = "Email Icon",
                keyboardType = KeyboardType.Email,
                enabled = !isLoading,
                isError = emailError != null,
                errorMessage = emailError
            )
            Spacer(modifier = Modifier.height(24.dp))
            CustomTextField(
                values = password,
                onValueChange = {
                    viewModel.updatePassword(it)
                },
                placeholder = "Password",
                icon = Icons.Default.Lock,
                contentDescription = "Password Icon",
                keyboardType = KeyboardType.Password,
                isPasswordField = true,
                enabled = !isLoading,
                isError = passwordError != null,
                errorMessage = passwordError
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    viewModel.login(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(6.dp),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text(
                    text = "No Account? Sign up ", style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    )
                )
                Text(
                    text = "Here",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.clickable {
                        navController.navigate(Routes.REGISTER)
                    })
            }
        }

        if (isLoading) {
            LoadingContent(text = "Memproses login...")
        }

    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    IngatinTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            LoginScreen(navController = navController)
        }
    }
}