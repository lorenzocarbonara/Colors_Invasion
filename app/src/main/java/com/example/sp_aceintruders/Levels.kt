package com.example.sp_aceintruders

data class Levels(
    val levelNumber: Int,
    val alienRows: Int,
    val alienColumns: Int,
    val alienSpeed: Float
)

fun getLevel(level: Int) : Levels {
    return when(level) {
        1 -> Levels(levelNumber = 1, alienRows = 3, alienColumns = 5, alienSpeed = 3f)
        2 -> Levels(levelNumber = 2, alienRows = 5, alienColumns = 5, alienSpeed = 3f)
        3 -> Levels(levelNumber = 3, alienRows = 6, alienColumns = 6, alienSpeed = 3f)
        else -> Levels(levelNumber = 1, alienRows = 3, alienColumns = 5, alienSpeed = 3f)
    }
}