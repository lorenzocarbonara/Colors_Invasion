package com.example.sp_aceintruders.DataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.OnConflictStrategy


@Dao
interface PlayerDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(player: Player)

    @Update
    fun update(player: Player)

    @Query("SELECT * FROM players WHERE username = :username")
    fun getPlayerByUsername(username: String): Player?

    @Query("Select * FROM players ORDER BY high_score DESC LIMIT 10")
    fun getTopPlayersLocal(): List<Player>
}