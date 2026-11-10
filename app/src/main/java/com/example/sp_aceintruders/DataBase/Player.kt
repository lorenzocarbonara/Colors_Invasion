package com.example.sp_aceintruders.DataBase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class Player(
    @PrimaryKey(autoGenerate = true) val _id: Int = 0,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "high_score") val highScore: Int = 0
)
