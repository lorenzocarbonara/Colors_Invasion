package com.example.sp_aceintruders.DataBase

import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Database

@Database(entities = [Player::class], version = 1)
abstract class Player_Database : RoomDatabase(){
    abstract fun playerDAO(): PlayerDAO

    companion object {
        @Volatile
        private var INSTANCE: Player_Database? = null
        fun getDatabase(context: Context): Player_Database {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    Player_Database::class.java, "sp-ACE_Intruders_Database"
                ).build()

            }
        }
    }
}