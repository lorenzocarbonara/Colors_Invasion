package com.example.sp_aceintruders

import android.media.SoundPool
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation


@Composable
fun Login(
    onLoginClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var username by rememberSaveable {mutableStateOf("")}
    var password by rememberSaveable {mutableStateOf("")}
    val context = LocalContext.current
    val buttonSoundPool_d = remember {SoundPool.Builder().setMaxStreams(2).build()}
    val buttonPress_d = remember {buttonSoundPool_d.load(context, R.raw.buttonpressed, 1)}

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = "Login Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.title_logo),
                contentDescription = "Logo",
                modifier = Modifier.fillMaxWidth(1f).padding(bottom = 32.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(32.dp))
            buttonGame(
                onClick = {
                    buttonSoundPool_d.play(buttonPress_d, 1f, 1f, 0, 0, 1f)
                    if (username.isNotBlank() && password.isNotBlank()) {
                        onLoginClick(username, password)
                    }
                },
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text("Play")
            }

        }
    }
}

