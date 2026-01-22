package com.example.fatfoxhospital.model

data class Nurse(
    val id: Long?,
    val name: String,
    val surname: String,
    val email: String,
    val user: String,
    val password: String,
    val profile: ByteArray? = null,
)