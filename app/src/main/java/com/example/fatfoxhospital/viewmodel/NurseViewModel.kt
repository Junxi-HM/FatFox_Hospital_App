package com.example.fatfoxhospital.viewmodel

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fatfoxhospital.R
import com.example.fatfoxhospital.backend.Connection
import com.example.fatfoxhospital.model.LoginRequest
import com.example.fatfoxhospital.model.Nurse
import com.example.fatfoxhospital.viewmodel.uistate.NurseListUiState
import com.example.fatfoxhospital.viewmodel.uistate.NurseUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

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

    private val _remoteNurse = MutableStateFlow<Nurse?>(null)
    val remoteNurse: StateFlow<Nurse?> = _remoteNurse


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

    // LIST


    // SEARCH
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
            R.drawable.perfil1,
            R.drawable.perfil2,
            R.drawable.perfil3,
            R.drawable.perfil4,
            R.drawable.perfil5,
            R.drawable.perfil7
        )

        fun getResIdFromByte(index: Byte): Int {
            return PROFILE_RESOURCES.getOrElse(index.toInt()) { R.drawable.logo }
        }

        fun getByteFromResId(resId: Int): Byte {
            val index = PROFILE_RESOURCES.indexOf(resId)
            return if (index != -1) index.toByte() else 0.toByte()
        }

    }

    // Backend
    // List
    var nurseListUiState: NurseListUiState by mutableStateOf(NurseListUiState.Loading);
    fun getAll() {
        viewModelScope.launch {
            nurseListUiState = NurseListUiState.Loading

            try {
                val nurseList: List<Nurse> = Connection.apiNurse.getAll()
                nurseListUiState = NurseListUiState.Success(nurseList)
                Log.d("Succesful: list", "Succesful connection to API")
            } catch (e: Exception) {
                Log.d("Failed: list", "Failed connection to API: ${e.message} ${e.printStackTrace()}")
                nurseListUiState = NurseListUiState.Error
            }
        }
    }

    // Search/Get by name
    var nurseUiState: NurseUiState by mutableStateOf(NurseUiState.Loading);
    fun searchNurse(name: String) {
        viewModelScope.launch {
            nurseUiState = NurseUiState.Loading

            try {
                val nurse: Nurse = Connection.apiNurse.searchNurse(name)
                nurseUiState = NurseUiState.Success(nurse)
                Log.d("Succesful: get by name: searchNurse", "Succesful connection to API")
            } catch (e: Exception) {
                nurseUiState = NurseUiState.Error
                Log.d("Failed: get by name", "Failed connection to API: ${e.message} ${e.printStackTrace()}")
            }
        }
    }

    // Get by username
    fun searchUser(username: String) {
        viewModelScope.launch {
            nurseUiState = NurseUiState.Loading

            try {
                val nurse: Nurse = Connection.apiNurse.searchUser(username)
                nurseUiState = NurseUiState.Success(nurse)
                Log.d("Succesful: search user", "Succesful connection to API")
            } catch (e: Exception) {
                nurseUiState = NurseUiState.Error
                Log.d("Failed: search user", "Failed connection to API: ${e.message} ${e.printStackTrace()}")
            }
        }
    }

    var loggedNurse: Nurse by mutableStateOf(Nurse(1, "", "", "", "", "", 0.toByte()))

    // Get by id
    fun getById(id: Long) {
        viewModelScope.launch {
            nurseUiState = NurseUiState.Loading

            try {
                val nurse: Nurse = Connection.apiNurse.getNurseById(id)
                nurseUiState = NurseUiState.Success(nurse)
                Log.d("Succesful: get by id", "Succesful connection to API")
            } catch (e: Exception) {
                nurseUiState = NurseUiState.Error
                Log.d("Failed: get by id", "Failed connection to API: ${e.message} ${e.printStackTrace()}")
            }
        }
    }

    // Login
    private val _loginEvent = Channel<Boolean>(Channel.BUFFERED)
    val loginEvent = _loginEvent.receiveAsFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val ok = loginAuthenticate(username, password)
            _loginEvent.send(ok)
        }
    }

    private suspend fun loginAuthenticate(username: String, password: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                Connection.apiNurse.login(LoginRequest(username, password))
                Log.d("Succesful: login", "Succesful connection to API")
                Connection.apiNurse.login(LoginRequest(username, password))
                    .isSuccessful
            } catch (e: Exception) {
                Log.d("Failed: login", "Failed connection to API")
                false
            }
        }

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
        searchNurse(newQuery)
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