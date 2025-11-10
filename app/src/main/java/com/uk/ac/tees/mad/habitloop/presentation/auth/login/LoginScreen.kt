package com.uk.ac.tees.mad.habitloop.presentation.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uk.ac.tees.mad.habitloop.R
import com.uk.ac.tees.mad.habitloop.domain.util.BiometricAuthStatus
import com.uk.ac.tees.mad.habitloop.domain.util.BiometricAuthenticator
import com.uk.ac.tees.mad.habitloop.ui.theme.HabitLoopTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginRoot(
    viewModel: LoginViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit,
    onGoToCreateAccount: () -> Unit,
    onGoToForgotPassword: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val biometricAuthenticator = remember { BiometricAuthenticator(context) }

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is LoginEvent.Failure -> {
                    scope.launch {
                        snackbarHostState.showSnackbar("Login Failed")
                    }
                }
                is LoginEvent.Success -> onLoginSuccess()
                is LoginEvent.GoToCreateAccount -> onGoToCreateAccount()
                is LoginEvent.GoToForgotPassword -> onGoToForgotPassword()
                is LoginEvent.ShowBiometricPrompt -> {
                    val status = biometricAuthenticator.isBiometricAuthAvailable()
                    if (status == BiometricAuthStatus.READY) {
                        biometricAuthenticator.promptBiometricAuth(
                            title = "Login",
                            subtitle = "Use your fingerprint to log in",
                            negativeButtonText = "Cancel",
                            onSuccess = { onLoginSuccess() },
                            onFailure = { scope.launch { snackbarHostState.showSnackbar("Biometric authentication failed.") } },
                            onError = { _, _ -> scope.launch { snackbarHostState.showSnackbar("An error occurred during biometric authentication.") } }
                        )
                    } else {
                        scope.launch {
                            val message = when(status) {
                                BiometricAuthStatus.NOT_AVAILABLE -> "Biometric authentication is not available on this device."
                                BiometricAuthStatus.TEMPORARILY_UNAVAILABLE -> "Biometric hardware is temporarily unavailable."
                                BiometricAuthStatus.AVAILABLE_BUT_NOT_ENROLLED -> "No biometrics enrolled. Please set up a fingerprint or screen lock in your device's settings."
                                else -> "An unknown error occurred with biometric authentication."
                            }
                            snackbarHostState.showSnackbar(message)
                        }
                    }
                }
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LoginScreen(
            modifier = Modifier.padding(padding),
            state = state,
            onAction = viewModel::onAction
        )
    }
}

@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Image(
            // Replace with your actual drawable resource
            painter = painterResource(id = R.drawable.login_image),
            contentDescription = "Login Illustration",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Welcome Back",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Text("Email", fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.email,
                onValueChange = { onAction(LoginAction.OnEmailChange(it)) },
                label = { Text("Enter your email") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Email Icon") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Password", fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.password,
                onValueChange = { onAction(LoginAction.OnPasswordChange(it)) },
                label = { Text("Enter your password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onAction(LoginAction.OnLoginClick) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5DB09B)),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("Login", fontSize = 16.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { onAction(LoginAction.OnCreateAccountClick) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = SolidColor(Color(0xFF5DB09B)))
        ) {
            Text("Create Account", color = Color.DarkGray, fontSize = 16.sp)
        }

        TextButton(onClick = { onAction(LoginAction.OnForgotPasswordClick) }) {
            Text("Forgot Password?", color = Color.Gray, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { onAction(LoginAction.OnUnlockWithFingerprintClick) }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    // Replace with your actual drawable resource
                    painter = painterResource(id = R.drawable.fingerprint_icon),
                    contentDescription = "Fingerprint Icon",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified // Use original colors of the icon
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Unlock with Fingerprint", color = Color.DarkGray, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "By logging in, you agree to our Terms of Service and Privacy Policy. Your data is encrypted and secure.",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    HabitLoopTheme {
        LoginScreen(
            state = LoginState(),
            onAction = {}
        )
    }
}
