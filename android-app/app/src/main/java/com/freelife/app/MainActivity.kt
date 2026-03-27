package com.freelife.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.freelife.app.repository.TokenRepository
import com.freelife.app.ui.GroupScreen
import com.freelife.app.ui.HomeScreen
import com.freelife.app.ui.LoginScreen
import com.freelife.app.ui.MapScreen
import com.freelife.app.ui.RegisterScreen
import com.freelife.app.ui.Screen
import com.freelife.app.ui.SettingsScreen
import com.freelife.app.ui.theme.FreeLifeTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val startDestination = if (TokenRepository(this).isLoggedIn()) {
            Screen.Home.route
        } else {
            Screen.Login.route
        }

        setContent {
            FreeLifeTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = startDestination) {
                    composable(Screen.Login.route) { LoginScreen(navController) }
                    composable(Screen.Register.route) { RegisterScreen(navController) }
                    composable(Screen.Home.route) { HomeScreen(navController) }
                    composable(
                        route = Screen.Map.route,
                        arguments = listOf(navArgument(Screen.Map.ARG_GROUP_ID) { type = NavType.IntType })
                    ) { backStackEntry ->
                        val groupId = backStackEntry.arguments?.getInt(Screen.Map.ARG_GROUP_ID)
                            ?: return@composable
                        MapScreen(navController, groupId)
                    }
                    composable(
                        route = Screen.Group.route,
                        arguments = listOf(navArgument(Screen.Group.ARG_GROUP_ID) { type = NavType.IntType })
                    ) { backStackEntry ->
                        val groupId = backStackEntry.arguments?.getInt(Screen.Group.ARG_GROUP_ID)
                            ?: return@composable
                        GroupScreen(navController, groupId)
                    }
                    composable(Screen.Settings.route) { SettingsScreen(navController) }
                }
            }
        }
    }
}
