package com.uk.ac.tees.mad.habitloop.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.uk.ac.tees.mad.habitloop.presentation.add_habbit.AddHabbitRoot
import com.uk.ac.tees.mad.habitloop.presentation.auth.create_account.CreateAccountRoot
import com.uk.ac.tees.mad.habitloop.presentation.auth.forgot.ForgotRoot
import com.uk.ac.tees.mad.habitloop.presentation.auth.login.LoginRoot
import com.uk.ac.tees.mad.habitloop.presentation.dashboard.DashboardRoot
import kotlinx.serialization.Serializable

@Composable
fun Navigation(navcontroller: NavHostController){
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) GraphRoutes.DashBoard else GraphRoutes.Login

    NavHost(navController = navcontroller, startDestination = startDestination){

        composable<GraphRoutes.Login>{
         LoginRoot(
             onLoginSuccess = {
                 navcontroller.navigate(GraphRoutes.DashBoard){
                     popUpTo(GraphRoutes.Login){
                         inclusive = true
                     }
                 }
                              },
             onGoToCreateAccount = { navcontroller.navigate(GraphRoutes.Register) },
             onGoToForgotPassword = { navcontroller.navigate(GraphRoutes.Forgot) }
         )
        }

        composable<GraphRoutes.Register>{
            CreateAccountRoot(
                onSignInClick = {
                    navcontroller.navigate(GraphRoutes.Login) {
                        popUpTo(GraphRoutes.Register) { inclusive = true }
                    }
                },
                onCreateAccountSuccess = {
                    navcontroller.navigate(GraphRoutes.DashBoard) {
                        popUpTo(GraphRoutes.Register) { inclusive = true }
                    }
                }
            )
        }

        composable<GraphRoutes.Forgot>{
            ForgotRoot(
                onBackToLogin = {
                    navcontroller.navigate(GraphRoutes.Login) {
                        popUpTo(GraphRoutes.Forgot) { inclusive = true }
                    }
                }
            )
        }

        composable<GraphRoutes.DashBoard>{
            DashboardRoot(navController = navcontroller)
        }

        composable<GraphRoutes.AddHabbit>{
            AddHabbitRoot(navController = navcontroller)
        }

        composable<GraphRoutes.Profile> { Text(text = "Profile Screen") }
        composable<GraphRoutes.Settings> { Text(text = "Settings Screen") }
        composable<GraphRoutes.Notifications> { Text(text = "Notifications Screen") }

    }

}

