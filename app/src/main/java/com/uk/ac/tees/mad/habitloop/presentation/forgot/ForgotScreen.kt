package com.uk.ac.tees.mad.habitloop.presentation.forgot

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uk.ac.tees.mad.habitloop.R
import com.uk.ac.tees.mad.habitloop.ui.theme.HabitLoopTheme

@Composable
fun ForgotRoot(
    viewModel: ForgotViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ForgotScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotScreen(
    state: ForgotState,
    onAction: (ForgotAction) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* No title */ },
                navigationIcon = {
                    IconButton(onClick = { onAction(ForgotAction.OnBackArrowClick) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.size(140.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.forgot_password_image), // Placeholder
                    contentDescription = "Forgot Password Icon",
                    modifier = Modifier.padding(24.dp).size(180.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Forgot\nPassword?",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Enter your email address to receive a password reset link.",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { onAction(ForgotAction.OnEmailChange(it)) },
                label = { Text("Enter your email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Icon"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onAction(ForgotAction.OnSubmitClick) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5DB09B)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Submit", fontSize = 16.sp, color = Color.White)
            }

            TextButton(onClick = { onAction(ForgotAction.OnBackToLoginClick) }) {
                Text(
                    "Back to Login",
                    color = Color(0xFF5DB09B),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    HabitLoopTheme {
        ForgotScreen(
            state = ForgotState(),
            onAction = {}
        )
    }
}
