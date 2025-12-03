package com.example.fatfoxhospital.pr07.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fatfoxhospital.pr07.data.model.Nurse

class NurseViewModel(application: Application) : AndroidViewModel(application) {

    private val _nurses = MutableLiveData<List<Nurse>>(getMockNurses())
    val nurses: LiveData<List<Nurse>> = _nurses

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _searchResults = MutableLiveData<List<Nurse>>(emptyList())
    val searchResults: LiveData<List<Nurse>> = _searchResults

    private val _selectedNurse = MutableLiveData<Nurse?>()
    val selectedNurse: LiveData<Nurse?> = _selectedNurse

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _searchResults.value = filterNurses(query)
    }

    fun selectNurse(nurse: Nurse) {
        _selectedNurse.value = nurse
    }

    fun clearSelectedNurse() {
        _selectedNurse.value = null
    }

    fun authenticate(username: String, password: String): Boolean {
        return _nurses.value?.any {
            it.user == username && it.password == password
        } ?: false
    }

    private fun filterNurses(query: String): List<Nurse> {
        if (query.isBlank()) return emptyList()
        val lowerQuery = query.lowercase()
        return _nurses.value?.filter {
            it.name.lowercase().contains(lowerQuery) ||
                    it.surname.lowercase().contains(lowerQuery) ||
                    it.user.lowercase().contains(lowerQuery)
        } ?: emptyList()
    }

    private fun getMockNurses(): List<Nurse> = listOf(
        Nurse(1, "Alice", "Johnson", "alice.j", "pass123"),
        Nurse(2, "Alina", "Kovacs", "alina.k", "qwerty"),
        Nurse(3, "Bob", "Smith", "bob.s", "abc123"),
        Nurse(4, "Charlie", "Brown", "charlie.b", "password123"),
        Nurse(5, "David", "Lee", "david.l", "letmein"),
        Nurse(6, "Emma", "Wilson", "emma.w", "secure456"),
        Nurse(7, "Fiona", "Garcia", "fiona.g", "medical789"),
        Nurse(8, "George", "Miller", "george.m", "hospital321")
    )
}