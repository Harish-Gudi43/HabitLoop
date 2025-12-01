package com.uk.ac.tees.mad.habitloop

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.uk.ac.tees.mad.habitloop.domain.AuthRepository
import com.uk.ac.tees.mad.habitloop.domain.QuoteRepository
import com.uk.ac.tees.mad.habitloop.domain.models.Quote
import com.uk.ac.tees.mad.habitloop.domain.util.Result
import com.uk.ac.tees.mad.habitloop.presentation.navigation.Navigation
import com.uk.ac.tees.mad.habitloop.ui.theme.HabitLoopTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val authRepository: AuthRepository by inject()
    private val quoteRepository: QuoteRepository by inject()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        lifecycleScope.launch {
            val user = authRepository.getCurrentUser().first()
            if (user?.motivationMode == true) {
                val quoteResult = quoteRepository.getQuote().first()
                if (quoteResult is Result.Success<*>) {
                    val quoteText = (quoteResult.data as? Quote)?.text
                    if (quoteText != null) {
                        Toast.makeText(this@MainActivity, quoteText, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        setContent {
            HabitLoopTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    Navigation(navcontroller = navController)
                }
            }
        }
    }
}
