package com.example.fatfoxhospital.backend

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Connection{
    private val connect = Retrofit.Builder().baseUrl("http://10.0.2.2:8080/").addConverterFactory(
        GsonConverterFactory.create()).build()

    val apiNurse: NurseApiEndpoints = connect.create(NurseApiEndpoints::class.java)
}