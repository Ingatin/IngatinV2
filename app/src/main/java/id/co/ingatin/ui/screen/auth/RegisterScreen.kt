package id.co.ingatin.ui.screen.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.co.ingatin.ui.common.UiState
import id.co.ingatin.ui.components.CustomTextField
import id.co.ingatin.ui.components.LoadingContent
import id.co.ingatin.ui.theme.IngatinTheme

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val username by viewModel.name.collectAsState()
    val nameError by viewModel.nameError.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()

    val registerState by viewModel.registState.collectAsState()

    val isLoading = registerState is UiState.Loading

    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is UiState.Success -> {
                Toast.makeText(context, state.data, Toast.LENGTH_SHORT).show()
                navController.navigate("home") {
                    popUpTo("register") { inclusive = true }
                }
            }
            is UiState.Error -> {
                Toast.makeText(context, "Gagal daftar: ${state.errorMessage}", Toast.LENGTH_SHORT).show()
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
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {
                    navController.navigate("login")
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(color = Color.LightGray)
                    .align(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Create an account",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Complete this a field to create a new account",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 14.sp
                )
            )
            Spacer(modifier = Modifier.height(45.dp))
            CustomTextField(
                values = username,
                onValueChange = {
                    viewModel.updateName(it)
                },
                placeholder = "Username",
                icon = Icons.Default.AccountCircle,
                contentDescription = "username Icon",
                keyboardType = KeyboardType.Text,
                enabled = !isLoading,
                isError = nameError != null,
                errorMessage = nameError
            )
            Spacer(modifier = Modifier.height(24.dp))
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
                    viewModel.register(username, email, password)
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
                    text = "Sign Up",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(6.dp),
                )
            }
        }

        if (isLoading) {
            LoadingContent(text = "Memproses pendaftaran...")
        }

    }


}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    IngatinTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            RegisterScreen(navController = navController)
        }
    }
}
