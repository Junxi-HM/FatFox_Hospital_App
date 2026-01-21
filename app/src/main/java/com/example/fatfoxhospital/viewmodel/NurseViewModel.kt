package com.example.fatfoxhospital.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.example.fatfoxhospital.model.Nurse
import com.example.fatfoxhospital.viewmodel.uistate.NurseListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

data class RegistrationUiState(
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val profile: Byte = 0,
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

    // LIST


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
        _uiState.value = _uiState.value.copy(profile = newIndex, errorMessage = null)
    }

    fun registerNurse() {
        viewModelScope.launch {
            val state = _uiState.value

            // Validación local de campos obligatorios
            if (state.name.isBlank() || state.surname.isBlank() || state.email.isBlank() ||
                state.username.isBlank() || state.password.isBlank()) {
                _uiState.value = state.copy(errorMessage = "Error: Todos los campos son obligatorios.")
                return@launch
            }

            try {
                val newNurse = Nurse(
                    id = null,
                    name = state.name.trim(),
                    surname = state.surname.trim(),
                    email = state.email.trim(),
                    user = state.username.trim(),
                    password = state.password,
                    profile = byteArrayOf(state.profile)
                )

                // Intento de registro
                val response = Connection.apiNurse.createNurse(newNurse)

                if (response.isSuccessful) {
                    // Éxito total
                    _uiState.value = state.copy(
                        isRegistrationSuccessful = true,
                        errorMessage = null
                    )
                    Log.d("API_SUCCESS", "Nurse created successfully")
                } else {
                    // El servidor respondió con un error
                    val errorBody = response.errorBody()?.string()
                    Log.e("API_ERROR", "Respuesta de error: $errorBody")
                    _uiState.value = state.copy(errorMessage = "Error del servidor: ${response.code()}")
                }

            } catch (e: Exception) {
                // Diagnosis del error de procesamiento
                Log.e("API_EXCEPTION", "Clase de error: ${e.javaClass.simpleName}")
                Log.e("API_EXCEPTION", "Mensaje: ${e.message}")

                // Si el error es "MalformedJsonException" o "EOFException", el problema es el formato de respuesta
                if (e is com.google.gson.JsonSyntaxException) {
                    _uiState.value = state.copy(errorMessage = "Error: El servidor envió un formato de datos incompatible.")
                } else {
                    _uiState.value = state.copy(errorMessage = "Error de conexión o datos: ${e.localizedMessage}")
                }
            }
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
        fun getResIdFromByte(profileByte: Byte): Int {
            val index = profileByte.toInt()
            return if (index in PROFILE_RESOURCES.indices) {
                PROFILE_RESOURCES[index]
            } else {
                R.drawable.perfil1 // 默认图
            }
        }
    }



    private fun getMockNurses(): List<Nurse> = listOf(
        Nurse(1, "Alice", "Johnson", "alice.johnson@fatfox.com", "alice.j", "pass123", byteArrayOf(0)),
        Nurse(2, "Alina", "Kovacs", "alina.kovacs@fatfox.com", "alina.k", "qwerty", byteArrayOf(1)),
        Nurse(3, "Bob", "Smith", "bob.smith@fatfox.com", "bob.s", "abc123", byteArrayOf(2)),
        Nurse(4, "Charlie", "Brown", "charlie.brown@fatfox.com", "charlie.b", "password123", byteArrayOf(1)),
        Nurse(5, "David", "Lee", "david.lee@fatfox.com", "david.l", "letmein", byteArrayOf(5)), // 修正了索引6，通常头像索引不超过5
        Nurse(6, "Emma", "Wilson", "emma.wilson@fatfox.com", "emma.w", "secure456", byteArrayOf(4)),
        Nurse(7, "Fiona", "Garcia", "fiona.garcia@fatfox.com", "fiona.g", "medical789", byteArrayOf(4)),
        Nurse(8, "George", "Miller", "george.miller@fatfox.com", "george.m", "hospital321", byteArrayOf(0))
    )


    // Backend
    // List
    var nurseListUiState: NurseListUiState by mutableStateOf(NurseListUiState.Loading);
    fun getAll(){
        viewModelScope.launch {
            nurseListUiState = NurseListUiState.Loading

            try {
                val nurseList: List<Nurse> = Connection.apiNurse.getAll()
                nurseListUiState = NurseListUiState.Success(nurseList)
                Log.d("Succesful","Succesful connection to API")
            }catch (e: Exception){
                Log.d("Failed", "Failed connection to API: ${e.message} ${e.printStackTrace()}")
                nurseListUiState = NurseListUiState.Error
            }
        }
    }
}