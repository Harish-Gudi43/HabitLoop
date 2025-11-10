package com.uk.ac.tees.mad.habitloop.presentation.auth.create_account

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uk.ac.tees.mad.habitloop.R
import com.uk.ac.tees.mad.habitloop.ui.theme.HabitLoopTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.bookly.domain.util.ObserveAsEvents

@Composable
fun CreateAccountRoot(
    viewModel: CreateAccountViewModel = koinViewModel(),
    onSignInClick: () -> Unit,
    onCreateAccountSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is CreateAccountEvent.Success -> onCreateAccountSuccess()
            is CreateAccountEvent.Failure -> scope.launch { snackbarHostState.showSnackbar("Failed to create account") }
            is CreateAccountEvent.GoToLogin -> onSignInClick()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
        CreateAccountScreen(
            state = state,
            onAction = viewModel::onAction,
            modifier = Modifier.padding(it)
        )
    }
}

@Composable
fun CreateAccountScreen(
    state: CreateAccountState,
    onAction: (CreateAccountAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier =Modifier.size(60.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.habitloop_logo1), // Placeholder
                contentDescription = "HabitLoop Logo",
                modifier = Modifier.size(60.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.habitloop_logo), // Placeholder
                contentDescription = "HabitLoop Logo",
                modifier = Modifier.size(60.dp)
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "HabitLoop",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5DB09B)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Create Your Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.create_account_image), // Placeholder
            contentDescription = "Create Account Illustration",
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Text("Name", fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.name,
                onValueChange = { onAction(CreateAccountAction.OnNameChange(it)) },
                label = { Text("Enter your name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name Icon") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Email", fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.email,
                onValueChange = { onAction(CreateAccountAction.OnEmailChange(it)) },
                label = { Text("Enter your email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Password", fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.password,
                onValueChange = { onAction(CreateAccountAction.OnPasswordChange(it)) },
                label = { Text("Enter your password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password Icon") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Confirm Password", fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = { onAction(CreateAccountAction.OnConfirmPasswordChange(it)) },
                label = { Text("Confirm your password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm Password Icon") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onAction(CreateAccountAction.OnCreateAccountClick) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5DB09B)),
            shape = RoundedCornerShape(8.dp)
        ) {
             if (state.isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("Create Account", fontSize = 16.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val annotatedString = buildAnnotatedString {
            append("Already have an account? ")
            pushStringAnnotation(tag = "SignIn", annotation = "SignIn")
            withStyle(style = SpanStyle(color = Color(0xFF5DB09B), fontWeight = FontWeight.Bold)) {
                append("Sign In")
            }
            pop()
        }

        ClickableText(
            text = annotatedString,
            onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "SignIn", start = offset, end = offset)
                    .firstOrNull()?.let {
                        onAction(CreateAccountAction.OnSignInClick)
                    }
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "By creating an account, you agree to our Privacy Policy.",
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
        CreateAccountScreen(
            state = CreateAccountState(),
            onAction = {}
        )
    }
}
