package com.example.sp_aceintruders

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class PlayerRequest(val username: String, val score: Int = 0, val mode: String)
data class ServerResponse(val message: String)
data class PlayerData(val username: String, val highScore: Int, val mode: String? = "Unknown")

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: PlayerRequest): ServerResponse

    @POST("update_score")
    suspend fun updateScore(@Body request: PlayerRequest): ServerResponse

    @GET("leaderboard")
    suspend fun getLeaderboard(): List<PlayerData>
}
private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
val retrofit = Retrofit.Builder().baseUrl("http://172.20.10.14:5000").addConverterFactory(MoshiConverterFactory.create(moshi)).build()
val apiService = retrofit.create(ApiService::class.java)