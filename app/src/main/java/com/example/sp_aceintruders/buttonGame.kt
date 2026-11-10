package com.example.sp_aceintruders

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun buttonGame(
    onClick: () -> (Unit),
    modifier: Modifier = Modifier,
    tintColor: Color = Color.Unspecified,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.padding(bottom = 8.dp).size(width = 250.dp, height = 80.dp).paint(
            painter = painterResource(id = R.drawable.button_game), colorFilter = if (tintColor != Color.Unspecified){
                ColorFilter.tint(tintColor, BlendMode.Modulate)
            } else null
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        content = content
    )
}