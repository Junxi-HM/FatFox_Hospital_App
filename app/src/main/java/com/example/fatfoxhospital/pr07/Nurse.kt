package com.example.fatfoxhospital.pr07

data class Nurse(
    var id: Long,
    var name: String,
    var surname: String,
    var email: String,
    var user: String,
    var password: String
)

val FAKE_REGISTERED_NURSES = listOf(
    Nurse(
        id = 1L,
        name = "Ana",
        surname = "Gómez",
        email = "ana.gomez@fatfox.com",
        user = "anagomez",
        password = "password1"
    ),
    Nurse(
        id = 2L,
        name = "Borja",
        surname = "Pérez",
        email = "borja.perez@fatfox.com",
        user = "borjap",
        password = "password2"
    )
    // Puedes añadir más ejemplos aquí
)