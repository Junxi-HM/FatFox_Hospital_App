package com.example.fatfoxhospital.pr07

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NurseViewModel : ViewModel() {
    // Lista observable de enfermeros
    private val _nurses = MutableStateFlow<List<Nurse>>(emptyList())
    val nurses: StateFlow<List<Nurse>> get() = _nurses

    init {
        // Datos de prueba
        _nurses.value = listOf(
            Nurse(1, "Alice", "Johnson", "alice.j", "pass123"),
            Nurse(2, "Alina", "Kovacs", "alina.k", "qwerty"),
            Nurse(3, "Bob", "Smith", "bob.s", "abc123"),
            Nurse(4, "Charlie", "Brown", "charlie.b", "password123"),
            Nurse(5, "David", "Lee", "david.l", "letmein")
        )
    }

    fun addNurse(nurse: Nurse) {
        _nurses.value = _nurses.value + nurse
    }

    fun validateNurseLogin(username: String, password: String): Boolean {
        return _nurses.value.any { it.user == username && it.password == password }
    }
}