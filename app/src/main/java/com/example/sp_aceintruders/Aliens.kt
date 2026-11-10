package com.example.sp_aceintruders

import androidx.compose.ui.graphics.Color


data class Alien(
    val x: Float,
    val y: Float,
    val color: Color,
    val movR: Boolean
)

fun genAliens(rows: Int, columns: Int, spacing: Float): List<Alien> {
    val newAliens = mutableListOf<Alien>()
    for (row in 0 until rows) {
        val rowColor = when(row % 3) {
            0 -> Color.Red
            1 -> Color.Green
            else -> Color.Blue
        }
        for (column in 0 until columns) {
            newAliens.add(
                Alien(
                    x = column * spacing,
                    y = row * 100f + 100f,
                    color = rowColor,
                    movR = true
                )
            )
        }
    }
    return newAliens
}

fun movAliens(currentAliens: List<Alien>, screenWidth: Float, speed: Float = 3f): List<Alien> {
    val alienWidth = 40f
    val dropDistance = 80f
    val update = mutableListOf<Alien>()

    val rows = currentAliens.groupBy {it.y}
    for ((_, aliensInRow) in rows) {
        val max_X = aliensInRow.maxOf {it.x}
        val min_X = aliensInRow.minOf {it.x}
        var movingR = aliensInRow.first().movR
        var drop = false

        if (movingR && max_X + alienWidth + speed > screenWidth) {
            movingR = false
            drop = true
        } else if (!movingR && min_X - speed < 0f) {
            movingR = true
            drop = true
        }

        for (alien in aliensInRow) {
            if (drop) {
                update.add(alien.copy(y = alien.y + dropDistance, movR = movingR))
            } else {
                val move_X  = if (movingR) speed else -speed
                update.add(alien.copy(x = alien.x + move_X, movR = movingR))
            }
        }
    }
    return update
}

fun genAliensEndlessMode(rows: Int, columns: Int, spacer: Float) : List<Alien> {
    val endlessAliens = mutableListOf<Alien>()
    val alienColors = listOf(Color.Red, Color.Green, Color.Blue)

    for (row in 0 until rows) {
        for (column in 0 until columns) {
            endlessAliens.add(
                Alien(
                    x = column * spacer,
                    y = row * 100f + 100f,
                    color = alienColors.random(),
                    movR = true
                )
            )
        }
    }
    return endlessAliens
}