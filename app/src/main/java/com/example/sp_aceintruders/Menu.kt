package com.example.sp_aceintruders

import android.media.MediaPlayer
import android.media.SoundPool
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun Menu(
    username: String?,
    onPlayLevelClick: () -> Unit,
    onPlayEndlessClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    modifier : Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val buttonSoundPool = remember {SoundPool.Builder().setMaxStreams(2).build()}
    val buttonPress = remember {buttonSoundPool.load(context, R.raw.buttonpressed, 1)}
    var settings by remember {mutableStateOf(false)}
    var MusicOn by remember {mutableStateOf(true)}
    var VibrationOn by remember {mutableStateOf(true)}
    var menuSymphony = remember { MediaPlayer.create(context, R.raw.menu_symphony).apply {
            isLooping = true
        }
    }

    LaunchedEffect(MusicOn) {
        if (MusicOn) {
            menuSymphony.start()
        } else {
            menuSymphony.pause()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver {_, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                if (menuSymphony.isPlaying) {
                    menuSymphony.pause()
                }
            } else if (event == Lifecycle.Event.ON_RESUME) {
                if (MusicOn) {
                    menuSymphony.start()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            menuSymphony.stop()
            menuSymphony.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.menu_screen),
            contentDescription = "Menu Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome $username, select a mode",
                fontSize = 25.sp,
                color = Color.Black,
                style = MaterialTheme.typography.headlineMedium.copy(
                    drawStyle = Stroke(
                        miter = 10f,
                        width = 8f,
                        join = StrokeJoin.Round
                    )
                )
            )
            Text(
                text = "Welcome $username, select a mode",
                fontSize = 25.sp,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(32.dp))
            buttonGame(onClick = {
                buttonSoundPool.play(buttonPress, 1f, 1f, 0, 0, 1f)
                onPlayLevelClick()
            }) {
                Text("Play Level Mode")
            }
            Spacer(modifier = Modifier.height(8.dp))
            buttonGame(onClick = {
                buttonSoundPool.play(buttonPress, 1f, 1f, 0, 0, 1f)
                onPlayEndlessClick()
            }) {

                Text("Play Endless Mode")
            }
            Spacer(modifier = Modifier.height(24.dp))
            buttonGame(onClick = {
                buttonSoundPool.play(buttonPress, 1f, 1f, 0, 0, 1f)
                onLeaderboardClick()
            },
                tintColor = Color.Magenta
            ) {
                Text("Global Ranking")
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
        IconButton(
            onClick = {settings = true},
            modifier = Modifier.align(Alignment.TopEnd).padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.LightGray
            )
        }
        if (settings) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 1f)).pointerInput(Unit) {}
            ) {
                Column (
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Settings",
                        color = Color.White,
                        fontSize = 50.sp,
                        fontWeight = Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    buttonGame(
                        onClick = {
                            buttonSoundPool.play(buttonPress, 1f, 1f, 0, 0, 1f)
                            MusicOn = !MusicOn
                        }
                    ) {
                        Text(if(MusicOn) "Music: ON" else "Music: OFF")
                    }
                    buttonGame(
                        onClick = {
                            buttonSoundPool.play(buttonPress, 1f, 1f, 0, 0, 1f)
                            VibrationOn = !VibrationOn
                        }
                    ) {
                        Text(if(VibrationOn) "Vibration: ON" else "Vibration: OFF")
                    }
                    Spacer(modifier = Modifier.padding(24.dp))
                    buttonGame(
                        onClick = {
                            buttonSoundPool.play(buttonPress, 1f, 1f, 0, 0, 1f)
                            settings = false
                        },
                        tintColor = Color.Red
                    ) {
                        Text("Close settings",
                            fontSize = 24.sp,
                            fontWeight = Bold,
                            color = Color.DarkGray)
                    }

                }
            }
        }
    }
}
