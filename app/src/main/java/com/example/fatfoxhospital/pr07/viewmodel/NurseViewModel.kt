package com.example.fatfoxhospital.pr07.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fatfoxhospital.R
import com.example.fatfoxhospital.pr07.model.Nurse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegistrationUiState(
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val username: String = "",
    val password: String = "",
    // Usamos R.drawable.logo como valor por defecto/placeholder
    val profileResId: Int = R.drawable.logo,
    val errorMessage: String? = null,
    val isRegistrationSuccessful: Boolean = false
)

class NurseViewModel(application: Application) : AndroidViewModel(application) {

    private val _nurses = MutableLiveData<List<Nurse>>(getMockNurses())
    val nurses: LiveData<List<Nurse>> = _nurses

    var numberNurses = (_nurses.value?.size ?: 0).toLong()

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _searchResults = MutableLiveData<List<Nurse>>(emptyList())
    val searchResults: LiveData<List<Nurse>> = _searchResults

    private val _selectedNurse = MutableLiveData<Nurse?>()
    val selectedNurse: LiveData<Nurse?> = _selectedNurse

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    private val existingEmails = listOf("alice.johnson@fatfox.com", "bob.smith@fatfox.com")
    private val existingUsers = listOf("alice.j", "bob.s")

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

    fun updateName(newName: String) {
        _uiState.value = _uiState.value.copy(name = newName, errorMessage = null)
    }

    fun updateSurname(newSurname: String) {
        _uiState.value = _uiState.value.copy(surname = newSurname, errorMessage = null)
    }

    fun updateEmail(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail.trim(), errorMessage = null)
    }

    fun updateUsername(newUsername: String) {
        _uiState.value = _uiState.value.copy(username = newUsername.trim(), errorMessage = null)
    }

    fun updatePassword(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword, errorMessage = null)
    }

    // Función para actualizar el ID del recurso de la foto de perfil
    fun updateProfileResId(newResId: Int) {
        _uiState.value = _uiState.value.copy(profileResId = newResId, errorMessage = null)
    }

    fun registerNurse() {
        viewModelScope.launch {
            val state = _uiState.value

            // 1. Validations
            if (state.name.isBlank() || state.surname.isBlank() || state.email.isBlank() || state.username.isBlank() || state.password.isBlank()) {
                _uiState.value =
                    state.copy(errorMessage = "Error: Todos los campos son obligatorios.")
                return@launch
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
                _uiState.value = state.copy(errorMessage = "Error: Formato de correo inválido.")
                return@launch
            }

            // Check against current list of nurses for email and username duplicates
            val currentNurses = _nurses.value ?: emptyList()

            if (currentNurses.any { it.email.equals(state.email, ignoreCase = true) }) {
                _uiState.value = state.copy(errorMessage = "Error: Correo ya registrado.")
                return@launch
            }

            if (currentNurses.any { it.user.equals(state.username, ignoreCase = true) }) {
                _uiState.value = state.copy(errorMessage = "Error: Usuario ya existe.")
                return@launch
            }


            // Generate a new ID (simple increment based on current size/max ID)
            val newId = (currentNurses.maxOfOrNull { it.id } ?: 0) + 1

            // Create the new Nurse object
            val newNurse = Nurse(
                id = newId,
                name = state.name.trim(),
                surname = state.surname.trim(),
                email = state.email.trim(),
                user = state.username.trim(),
                password = state.password,
                profileResId = state.profileResId // Usa el ID de recurso de la foto
            )

            // Add the new nurse to the list
            _nurses.value = currentNurses + newNurse
            numberNurses = _nurses.value?.size?.toLong() ?: 0L

            // 2. Success
            _uiState.value = state.copy(isRegistrationSuccessful = true)
        }
    }

    fun registrationComplete() {
        _uiState.value = RegistrationUiState()
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
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
        // Modificado: Añadido el profileResId
        Nurse(1, "Alice", "Johnson", "alice.johnson@fatfox.com", "alice.j", "pass123", R.drawable.perfil1),
        Nurse(2, "Alina", "Kovacs", "alina.kovacs@fatfox.com", "alina.k", "qwerty", R.drawable.perfil2),
        Nurse(3, "Bob", "Smith", "bob.smith@fatfox.com", "bob.s", "abc123", R.drawable.perfil3),
        Nurse(4, "Charlie", "Brown", "charlie.brown@fatfox.com", "charlie.b", "password123", R.drawable.perfil2),
        Nurse(5, "David", "Lee", "david.lee@fatfox.com", "david.l", "letmein", R.drawable.perfil7),
        Nurse(6, "Emma", "Wilson", "emma.wilson@fatfox.com", "emma.w", "secure456", R.drawable.perfil5),
        Nurse(7, "Fiona", "Garcia", "fiona.garcia@fatfox.com", "fiona.g", "medical789", R.drawable.perfil5),
        Nurse(8, "George", "Miller", "george.miller@fatfox.com", "george.m", "hospital321", R.drawable.perfil1)
    )
}