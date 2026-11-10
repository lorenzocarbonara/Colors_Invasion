package com.example.sp_aceintruders

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class ColorBullet(
    val x: Float,
    val y: Float,
    val color: Color
)
fun moveShipBullet(bullets: List<ColorBullet>) : List<ColorBullet> {
    return bullets.map {it.copy(y = it.y - 25f)}
}

fun moveAlienBullet(alienBullets: List<Offset>, screenHeight: Float, currentLevel: Int): List<Offset> {
    val speed = 10f + (currentLevel * 2f)
    return alienBullets.map {it.copy(y = it.y + speed)}.filter {it.y < screenHeight}
}

fun alienShoot(aliens: List<Alien>, currentLevel: Int): Offset? {
    if(aliens.isNotEmpty() && Math.random() < (0.015 * currentLevel)) {
        val randomAlien = aliens.random()
        return Offset(randomAlien.x + 15f, randomAlien.y + 40f)
    }
    return null
}

fun shipDamaged(alienBullets: List<Offset>, shipX: Float, screenHeight: Float) : Boolean {
    val shipY = screenHeight - 150f
    for (alienBullet in alienBullets) {
        if (alienBullet.x < shipX + 80f &&
            alienBullet.x + 10f > shipX &&
            alienBullet.y < shipY + 50f &&
            alienBullet.y + 10f > shipY
            ) {
            return true
        }
    }
    return false
}