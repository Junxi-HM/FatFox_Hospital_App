package com.example.fatfoxhospital.pr07.model

data class Nurse(
    val id: Long,
    val name: String,
    val surname: String,
    val email: String,
    val user: String,
    val password: String,
    val profileResId: Int
)