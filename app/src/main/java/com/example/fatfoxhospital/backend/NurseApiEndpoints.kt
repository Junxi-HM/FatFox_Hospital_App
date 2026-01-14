package com.example.fatfoxhospital.backend

import com.example.fatfoxhospital.model.Nurse
import retrofit2.http.GET
import retrofit2.http.Path

interface NurseApiEndpoints {
    @GET("nurse")
    suspend fun getNurse(@Path("id") id: Int): Nurse
}