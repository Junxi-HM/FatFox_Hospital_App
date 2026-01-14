package com.example.fatfoxhospital.backend

import com.example.fatfoxhospital.model.Nurse
import retrofit2.Response
import retrofit2.http.*

interface NurseApiEndpoints {
    // LOGIN: @PostMapping("/login")
    @POST("nurse/login")
    suspend fun login(@Body nurse: Nurse): Response<Boolean>

    // READ BY ID: @GetMapping("/{id}")
    @GET("nurse/{id}")
    suspend fun getNurse(@Path("id") id: Long): Response<Nurse>

    // UPDATE: @PutMapping("/{id}")
    @PUT("nurse/{id}")
    suspend fun updateNurse(@Path("id") id: Long, @Body nurse: Nurse): Response<Nurse>

    // DELETE: @DeleteMapping("/{id}")
    @DELETE("nurse/{id}")
    suspend fun deleteNurse(@Path("id") id: Long): Response<Void>
}