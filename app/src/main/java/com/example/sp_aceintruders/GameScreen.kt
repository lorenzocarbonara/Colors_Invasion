package com.example.sp_aceintruders

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import android.hardware.SensorEventListener
import android.hardware.SensorEvent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage


@Composable
fun LevelMode(username: String?, onNavigateToMenu: () -> Unit) {
    val context = LocalContext.current
    val vibrator = remember {context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }
    val mediaPlayer = remember {MediaPlayer.create(context, R.raw.soundtrack).apply {
        isLooping = true }
    }
    val victoryJingle = remember {MediaPlayer.create(context, R.raw.victoryjingle)}
    val defeatJingle = remember {MediaPlayer.create(context, R.raw.defeatjingle)}
    val maxLaser = remember { SoundPool.Builder().setMaxStreams(5).build()}
    val laserSound = remember {maxLaser.load(context, R.raw.laser, 1)}
    val alienHit = remember {maxLaser.load(context, R.raw.alienhit, 1)}

    var shipX by remember {mutableFloatStateOf(500f)}
    var tiltX by remember {mutableFloatStateOf(0f)}
    var width by remember {mutableFloatStateOf(1000f)}
    var aliens by remember { mutableStateOf(genAliens(rows = 3, columns = 5, spacing = 120f)) }
    var bullets by remember {mutableStateOf(listOf<ColorBullet>())}
    var currentShipColor by remember {mutableStateOf(Color.Blue)}
    var score by remember {mutableIntStateOf(0)}
    var height by remember {mutableFloatStateOf(2000f)}
    var state by remember {mutableStateOf("PLAYING")}

    val spaceShuttle = ImageBitmap.imageResource(id = R.drawable.space_shuttle)
    val spaceShuttleRed = ImageBitmap.imageResource(id = R.drawable.space_shuttle_red)
    val spaceShuttleGreen = ImageBitmap.imageResource(id = R.drawable.space_shuttle_green)
    val alienOne = ImageBitmap.imageResource(id = R.drawable.alien_one)
    val alienOneRed = ImageBitmap.imageResource(id = R.drawable.alien_one_red)
    val alienOneGreen = ImageBitmap.imageResource(id = R.drawable.alien_one_green)
    val bulletBlue = ImageBitmap.imageResource(id = R.drawable.bullet_blue)
    val bulletRed = ImageBitmap.imageResource(id = R.drawable.bullet_red)
    val bulletGreen = ImageBitmap.imageResource(id = R.drawable.bullet_green)

    var alienBullets by remember {mutableStateOf(listOf<Offset>())}

    val buttonSoundPool_b = remember {SoundPool.Builder().setMaxStreams(2).build()}
    val buttonPress_b = remember {buttonSoundPool_b.load(context, R.raw.buttonpressed, 1)}
    var menuOpen by remember {mutableStateOf(false)}
    var MusicOn by remember {mutableStateOf(true)}
    var VibrationOn by remember {mutableStateOf(true)}

    var currentLevel by remember {mutableStateOf(1)}
    val maxLevels = 3



    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event != null) {
                    tiltX = event.values[0]
                }
            }
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
        onDispose {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.release()
            victoryJingle.release()
            defeatJingle.release()
            maxLaser.release()
            buttonSoundPool_b.release()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver {_, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                }
            } else if (event == Lifecycle.Event.ON_RESUME) {
                if (state == "PLAYING" && !mediaPlayer.isPlaying) {
                    mediaPlayer.start()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(state, menuOpen) {
        while (state == "PLAYING" && !menuOpen) {
            shipX -= tiltX * 12f
            if (shipX < 0f) shipX = 0f
            if (shipX > width - 100f) shipX = width - 100f

            val currentLevelPlaying = getLevel(currentLevel)
            val movBullets = moveShipBullet(bullets)
            val colBullets = movBullets.toMutableList()

            val movementAliens = movAliens(aliens, width, speed = currentLevelPlaying.alienSpeed)
            val colAliens = mutableListOf<Alien>()

            for (alien in movementAliens) {
                val hitboxBullet = colBullets.find { bullet ->
                            bullet.x < alien.x + 80f &&
                            bullet.x + 10f > alien.x &&
                            bullet.y < alien.y + 40f &&
                            bullet.y + 30f > alien.y &&
                                    bullet.color == alien.color

                }
                if (hitboxBullet != null) {
                    colBullets.remove(hitboxBullet)
                    score += 10

                    if (MusicOn) {
                        maxLaser.play(alienHit, 1f, 1f, 0, 0, 1f)
                    }

                    if (VibrationOn) {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator.vibrate(50)
                        }
                    }
                } else {
                    colAliens.add(alien)
                }
            }

            val movAlienBullets = moveAlienBullet(alienBullets, height, currentLevel).toMutableList()
            val newBullet = alienShoot(colAliens, currentLevel)
            if (newBullet != null) {
                movAlienBullets.add(newBullet)
            }
            if (shipDamaged(movAlienBullets, shipX, height)) {
                state = "GAME OVER"
            }
            aliens = colAliens
            bullets = colBullets
            alienBullets = movAlienBullets

            if (aliens.isEmpty()) {
                if (currentLevel < maxLevels) {
                    state = "LEVEL_CLEARED"
                } else {
                    state = "VICTORY"
                }
            } else if (aliens.any {it.y > height - 150f}) {
                state = "GAME OVER"
            }
            delay(14)
        }

    }

    LaunchedEffect(state) {
        if (state == "PLAYING") {
            if (defeatJingle.isPlaying) {
                defeatJingle.pause()
            }
            if (MusicOn && !mediaPlayer.isPlaying) {
                mediaPlayer.start()
            }
        } else {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
            if (state == "VICTORY") {
                victoryJingle.seekTo(0)
                victoryJingle.start()
            } else if (state == "GAME OVER") {
                defeatJingle.seekTo(0)
                defeatJingle.start()
            }
            if (username != null) {
                    try {
                        val request = PlayerRequest(username = username, score = score, mode = "Level Mode")
                        val response = apiService.updateScore(request)
                        android.util.Log.d("Retrofit", "Server: ${response.message}")
                    } catch (e: Exception) {
                        android.util.Log.d("Retrofit", "Connection Error: ${e.message}")
                    }
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        val gameBackground = remember {coil.ImageLoader.Builder(context)
            .components {
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    add(coil.decode.ImageDecoderDecoder.Factory())
                } else {
                    add(coil.decode.GifDecoder.Factory())
                }
            }.build()
        }
        AsyncImage(
            model = R.drawable.space,
            imageLoader = gameBackground,
            contentDescription = "backgorund",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        val newBullet = ColorBullet(x = shipX + 45f, y = size.height - 150f, color = currentShipColor)
                        bullets = bullets + newBullet
                        if (state == "PLAYING" && !menuOpen && MusicOn) {
                            maxLaser.play(laserSound, 1f, 1f, 0, 0, 1f)
                        }
                    }
                )
            }) {

            width = size.width
            height = size.height

            bullets.forEach {bullet ->
                val chooseBullet = when (bullet.color) {
                    Color.Red -> bulletRed
                    Color.Green -> bulletGreen
                    else -> bulletBlue
                }
                drawImage(
                    image = chooseBullet,
                    dstOffset = IntOffset(bullet.x.toInt(), bullet.y.toInt()),
                    dstSize = IntSize(10, 30)
                )
            }

            alienBullets.forEach{alienBullet ->
                drawCircle(
                    color = Color.White,
                    radius = 5f,
                    center = alienBullet
                )
            }


            aliens.forEach {alienCoordinate ->
                val colorAlien = when (alienCoordinate.color) {
                    Color.Red -> alienOneRed
                    Color.Green -> alienOneGreen
                    else -> alienOne
                }
                drawImage(
                    image = colorAlien,
                    dstOffset = IntOffset(alienCoordinate.x.toInt(), alienCoordinate.y.toInt()),
                    dstSize = IntSize(40, 40)
                )
            }

            val colorShuttle = when (currentShipColor) {
                Color.Red -> spaceShuttleRed
                Color.Green -> spaceShuttleGreen
                else -> spaceShuttle
            }
            drawImage(
                image = colorShuttle,
                dstOffset = IntOffset(shipX.toInt(), (size.height - 150f).toInt()),
                dstSize = IntSize(80, 50)
            )
        }
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Score: $score",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Level: $currentLevel",
                color = Color.LightGray,
                fontSize = 20.sp,
                fontWeight = Bold
            )
        }

        IconButton(onClick = {menuOpen = true}, modifier = Modifier.align(TopEnd).padding(16.dp)) {
            Icon(imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = Color.White
            )
        }
        if (menuOpen) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.8f)).pointerInput(Unit) {}
            ) {
                Column(
                    modifier = Modifier.align(Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Paused",
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    buttonGame(onClick = {
                        buttonSoundPool_b.play(buttonPress_b, 1f, 1f, 0, 0, 1f)
                        menuOpen = false
                    },
                        tintColor = Color.Green
                    ) {
                        Text("Resume", fontSize = 20.sp, fontWeight = Bold)
                    }
                    buttonGame(onClick = {
                        buttonSoundPool_b.play(buttonPress_b, 1f, 1f, 0, 0, 1f)
                        MusicOn = !MusicOn
                        if (MusicOn) {
                            mediaPlayer.start()
                        } else {
                            mediaPlayer.pause()
                        }
                    }) {
                        Text(if (MusicOn) "Music: ON" else "Music: OFF", fontSize = 20.sp, fontWeight = Bold)
                    }
                    buttonGame(onClick = {
                        buttonSoundPool_b.play(buttonPress_b, 1f, 1f, 0, 0, 1f)
                        VibrationOn = !VibrationOn
                    }) {
                        Text(if (VibrationOn) "Vibration: ON" else "Vibration: OFF", fontSize = 20.sp, fontWeight = Bold)
                    }
                    buttonGame(
                        onClick = {
                            menuOpen = false
                            onNavigateToMenu()
                        },
                        tintColor = Color.Red
                    ) {
                        Text("Main Menu", fontSize = 20.sp, fontWeight = Bold)
                    }
                }
            }
        }

        if (state != "PLAYING") {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.8f)),
                contentAlignment = Center
            ) {
                Column(
                    modifier = Modifier.align(Center),
                    horizontalAlignment = CenterHorizontally
                ) {
                    val titleText = when (state) {
                        "VICTORY" -> "YOU WON"
                        "LEVEL_CLEARED" -> "LEVEL CLEARED"
                        else -> "GAME OVER"
                    }
                    val titleColor = when (state) {
                        "VICTORY" -> Color.Green
                        "LEVEL_CLEARED" -> Color.Green
                        else -> Color.Red
                    }
                    Text(
                        text = titleText,
                        color = titleColor,
                        fontSize = 48.sp,
                        fontWeight = Bold
                    )

                    Text(
                        text = "Final Score: $score",
                        color = Color.White,
                        fontSize = 24.sp
                    )
                    buttonGame(
                        onClick = {
                            buttonSoundPool_b.play(buttonPress_b, 1f, 1f, 0, 0, 1f)
                            if (state == "LEVEL_CLEARED") {
                                currentLevel++
                                alienBullets = emptyList()
                            } else {
                                currentLevel = 1
                                alienBullets = emptyList()
                                score = 0
                            }
                            val level_settings = getLevel(currentLevel)
                            val modSpacing = if (level_settings.alienColumns >= 6) 80f else 120f
                            aliens = genAliens(
                                rows = level_settings.alienRows,
                                columns = level_settings.alienColumns,
                                spacing = modSpacing
                            )
                            shipX = width / 2 - 50f
                            bullets = emptyList()
                            state = "PLAYING"
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(
                            text = if (state == "LEVEL_CLEARED") "NEXT LEVEL" else "REPLAY",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Do you surrend? ",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    buttonGame(
                        onClick = {
                            buttonSoundPool_b.play(buttonPress_b, 1f, 1f, 0, 0, 1f)
                            onNavigateToMenu() },
                        tintColor = Color.Red
                    ) {
                        Text(
                            text = "MAIN MENU",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = Bold
                        )
                    }
                }
            }
        }
        
        if (state == "PLAYING" && !menuOpen) {
            Column(
                modifier = Modifier.align(CenterEnd).padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val colors = listOf(Color.Red, Color.Green, Color.Blue)
                colors.forEach {buttonColor ->
                    var selectedColor = (currentShipColor == buttonColor)
                    val iconImage = when (buttonColor) {
                        Color.Red -> spaceShuttleRed
                        Color.Green -> spaceShuttleGreen
                        else -> spaceShuttle
                    }
                    IconButton(
                        onClick = {currentShipColor = buttonColor},
                        modifier = Modifier.size(65.dp)
                    ) {
                        Image(
                            bitmap = iconImage,
                            contentDescription = "Change Space Shuttles Color",
                            modifier = Modifier.fillMaxSize().alpha(if(selectedColor) 1f else 0.3f)
                        )
                    }
                }
            }
        }
    }


}