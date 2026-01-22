package com.example.fatfoxhospital.backend

import com.example.fatfoxhospital.model.LoginRequest
import com.example.fatfoxhospital.model.Nurse
import retrofit2.Response
import retrofit2.http.*

interface NurseApiEndpoints {
    // LIST: @GetMapping("/index")
    @GET("nurse/index")
    suspend fun getAll(): List<Nurse>

    // LOGIN: @PostMapping("/login")
    @POST("nurse/login")
    suspend fun login(@Body req: LoginRequest): Response<Boolean>

    // REGISTER: @PostMapping("/new")
    @POST("nurse/new")
    suspend fun createNurse(@Body nurse: Nurse): Response<Void>

    // READ BY ID: @GetMapping("/{id}")
    @GET("nurse/{id}")
    suspend fun getNurseById(@Path("id") id: Long): Nurse

    // GET BY NAME: @GetMapping("/name/{name}")
    @GET("nurse/name/{name}")
    suspend fun searchNurse(@Path("name") name: String): Nurse

    // GET BY USERNAME: @GetMapping("/user/{user}")
    @GET("nurse/user/{user}")
    suspend fun searchUser(@Path("user") user: String): Nurse

    // UPDATE: @PutMapping("/{id}")
    @PUT("nurse/{id}")
    suspend fun updateNurse(@Path("id") id: Long, @Body nurse: Nurse): Response<Nurse>

    // DELETE: @DeleteMapping("/{id}")
    @DELETE("nurse/{id}")
    suspend fun deleteNurse(@Path("id") id: Long): Response<Void>
}