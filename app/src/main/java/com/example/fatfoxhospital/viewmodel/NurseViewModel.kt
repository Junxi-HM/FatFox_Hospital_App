package com.example.fatfoxhospital.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fatfoxhospital.R
import com.example.fatfoxhospital.model.Nurse
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
    val profileRes: Byte = 0,
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

    // SEARCH
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _searchResults.value = filterNurses(query)
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

    // DETAIL
    fun selectNurse(nurse: Nurse) {
        _selectedNurse.value = nurse
    }

    fun clearSelectedNurse() {
        _selectedNurse.value = null
    }

    // LOGIN
    fun loginAuthenticate(username: String, password: String): Boolean {
        return _nurses.value?.any {
            it.user == username && it.password == password
        } ?: false
    }

    // REGISTRATION
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

    fun updateProfileRes(newIndex: Byte) {
        _uiState.value = _uiState.value.copy(profileRes = newIndex, errorMessage = null)
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
                profileRes = state.profileRes
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

    companion object {
        val PROFILE_RESOURCES = listOf(
            R.drawable.perfil1, // 索引 0
            R.drawable.perfil2, // 索引 1
            R.drawable.perfil3, // 索引 2
            R.drawable.perfil4, // 索引 3
            R.drawable.perfil5, // 索引 4
            R.drawable.perfil7  // 索引 5
        )

        // 辅助方法：通过索引获取资源ID
        fun getResIdFromByte(index: Byte): Int {
            return PROFILE_RESOURCES.getOrElse(index.toInt()) { R.drawable.logo }
        }

        // 辅助方法：将资源 ID 转换为 Byte 索引
        fun getByteFromResId(resId: Int): Byte {
            val index = PROFILE_RESOURCES.indexOf(resId)
            return if (index != -1) index.toByte() else 0.toByte()
        }

    }



    private fun getMockNurses(): List<Nurse> = listOf(
        // Modificado: Añadido el profileResId
        Nurse(
            1,
            "Alice",
            "Johnson",
            "alice.johnson@fatfox.com",
            "alice.j",
            "pass123",
            0
        ),
        Nurse(
            2,
            "Alina",
            "Kovacs",
            "alina.kovacs@fatfox.com",
            "alina.k",
            "qwerty",
            1
        ),
        Nurse(3, "Bob", "Smith", "bob.smith@fatfox.com", "bob.s", "abc123", 2),
        Nurse(
            4,
            "Charlie",
            "Brown",
            "charlie.brown@fatfox.com",
            "charlie.b",
            "password123",
            1
        ),
        Nurse(5, "David", "Lee", "david.lee@fatfox.com", "david.l", "letmein", 6),
        Nurse(
            6,
            "Emma",
            "Wilson",
            "emma.wilson@fatfox.com",
            "emma.w",
            "secure456",
            4
        ),
        Nurse(
            7,
            "Fiona",
            "Garcia",
            "fiona.garcia@fatfox.com",
            "fiona.g",
            "medical789",
            4
        ),
        Nurse(
            8,
            "George",
            "Miller",
            "george.miller@fatfox.com",
            "george.m",
            "hospital321",
            0
        )
    )
}