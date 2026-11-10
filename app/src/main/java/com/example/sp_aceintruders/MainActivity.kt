package com.example.sp_aceintruders

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sp_aceintruders.DataBase.Player
import com.example.sp_aceintruders.DataBase.Player_Database
import com.example.sp_aceintruders.ui.theme.SpACEIntrudersTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = Player_Database.getDatabase(this)
        val dao = db.playerDAO()

        setContent {
            SpACEIntrudersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            Login(
                                onLoginClick = {username, _->
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        var player = dao.getPlayerByUsername(username)
                                        if (player == null) {
                                            player = Player(username = username, highScore = 0)
                                            dao.insert(player)
                                        }
                                        withContext(Dispatchers.Main) {
                                            navController.navigate("menu/$username")
                                        }
                                    }
                                }
                            )
                        }
                        composable("menu/{username}") {backStackEntry ->
                            val user = backStackEntry.arguments?.getString("username")
                            Menu(
                                username = user,
                                onPlayLevelClick = {
                                    navController.navigate("game/$user")
                                },
                                onPlayEndlessClick = {
                                    navController.navigate("endless/$user")
                                },
                                onLeaderboardClick = {
                                    navController.navigate("LeaderBoard")
                                }
                            )
                        }
                        composable("game/{username}") { backStackEntry ->
                            val user = backStackEntry.arguments?.getString("username")
                            LevelMode(
                                username = user,
                                onNavigateToMenu = {
                                    navController.popBackStack("menu/$user", inclusive = false)
                                }
                            )
                        }

                        composable("endless/{username}") {backStackEntry ->
                            val user = backStackEntry.arguments?.getString("username")
                            EndlessMode(
                                username = user,
                                onNavigateToMenu = {
                                    navController.popBackStack("menu/$user", inclusive = false)
                                }
                            )
                        }

                        composable("leaderboard") {
                            Leaderboard(
                                onBackToMenu = {
                                    navController.popBackStack()
                                }
                            )
                        }


                    }
                }

            }
        }
    }
}