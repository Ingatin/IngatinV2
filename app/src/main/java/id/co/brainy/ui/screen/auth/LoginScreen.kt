package id.co.brainy.ui.screen.auth

import android.util.Log
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import id.co.brainy.ui.ViewModelFactory
import id.co.brainy.ui.common.UiState
import id.co.brainy.ui.components.CustomTextField
import id.co.brainy.ui.theme.BrainyTheme

@Composable
fun LoginScreen(
    navController: NavController
) {

    val context = LocalContext.current
    val factory = remember { ViewModelFactory(context)}
    val viewModel: AuthViewModel = viewModel(factory = factory)

    val loginState by viewModel.loginState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is UiState.Empty -> {
                isLoading = false
            }

            is UiState.Loading -> {
                isLoading = true
                Log.d("LoginScreen", "Loading...")
            }

            is UiState.Success -> {
                isLoading = false

                val data = state.data
                Log.d("LoginScreen", "Login berhasil: $data")
                Toast.makeText(context, "Login berhasil", Toast.LENGTH_SHORT).show()

                navController.navigate("home")

            }

            is UiState.Error -> {
                isLoading = false
                val errorMessage = state.errorMessage

                Log.e("LoginScreen", "Login gagal: $errorMessage")
                Toast.makeText(context, "Gagal Login: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Sign In",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 40.sp, fontWeight = FontWeight.ExtraBold
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
                    email = it
                },
                placeholder = "Email",
                icon = Icons.Default.Email,
                contentDescription = "Email Icon",
                keyboardType = KeyboardType.Email,
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.height(24.dp))
            CustomTextField(
                values = password,
                onValueChange = {
                    password = it
                },
                placeholder = "Password",
                icon = Icons.Default.Lock,
                contentDescription = "Password Icon",
                keyboardType = KeyboardType.Password,
                isPasswordField = true,
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    viewModel.login(email,password)
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
                Text(text = "Here",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.clickable {
                        navController.navigate("register")
                    })
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center),
                color = MaterialTheme.colorScheme.tertiary
            )
        }

    }


}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    BrainyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            LoginScreen(navController = navController)
        }
    }
}