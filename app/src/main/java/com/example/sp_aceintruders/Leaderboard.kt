package com.example.sp_aceintruders

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Leaderboard(onBackToMenu: () -> Unit) {
    var players by remember {mutableStateOf<List<PlayerData>>(emptyList())}
    var loading by remember {mutableStateOf(true)}

    LaunchedEffect(Unit) {
            try {
                val response = apiService.getLeaderboard()
                withContext(Dispatchers.Main) {
                    players = response
                    loading = false
                }
            } catch (e: Exception) {
                loading = false
                Log.e("Leaderboard", "Error Download: ${e.message}")
            }
    }
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxSize()){
            Text(
                text ="Global Ranking",
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 24.sp,
                fontWeight = Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp).align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (loading) {
                CircularProgressIndicator()
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "RANK: ",
                        color = Color.Yellow,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "NAME: ",
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "SCORE: ",
                        color = Color.Blue,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "MODE: ",
                        color = Color.Magenta,
                        modifier = Modifier.weight(0.8f)
                    )
                }
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(players) { index, player ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${index + 1}.${player.username}",
                                color = Color.White,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = player.highScore.toString(),
                                color = Color.White,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = player.mode ?: "N/A",
                                color = Color.Green,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            buttonGame(onClick = onBackToMenu, modifier = Modifier.fillMaxWidth()) {
                Text("Main Menu")
            }
        }
    }
}