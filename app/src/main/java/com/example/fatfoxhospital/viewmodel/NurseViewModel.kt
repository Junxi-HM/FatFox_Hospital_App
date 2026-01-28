package com.example.fatfoxhospital.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
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
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.withContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

data class RegistrationUiState(
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val profile: ByteArray? = null,
    val profileIndex: Int = 0,
    val errorMessage: String? = null,
    val isRegistrationSuccessful: Boolean = false
)

// UI State for Update operations
data class UpdateUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

// UI State for Delete operations
data class DeleteUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class NurseViewModel(application: Application) : AndroidViewModel(application) {

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery
    private val _selectedNurse = MutableLiveData<Nurse?>()
    val selectedNurse: LiveData<Nurse?> = _selectedNurse
    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    // Update UI State
    private val _updateUiState = MutableStateFlow(UpdateUiState())
    val updateUiState: StateFlow<UpdateUiState> = _updateUiState.asStateFlow()

    // Delete UI State
    private val _deleteUiState = MutableStateFlow(DeleteUiState())
    val deleteUiState: StateFlow<DeleteUiState> = _deleteUiState.asStateFlow()

    // DETAIL
    fun selectNurse(nurse: Nurse) {
        _selectedNurse.value = nurse
    }

    fun clearSelectedNurse() {
        _selectedNurse.value = null
    }

    // REGISTRATION
    fun updateName(newName: String) {
        _uiState.value = _uiState.value.copy(name = newName)
    }

    fun updateSurname(newSurname: String) {
        _uiState.value = _uiState.value.copy(surname = newSurname)
    }

    fun updateEmail(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail.trim())
    }

    fun updateUsername(newUsername: String) {
        _uiState.value = _uiState.value.copy(username = newUsername.trim())
    }

    fun updatePassword(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword)
    }

    fun registrationComplete() {
        _uiState.value = RegistrationUiState()
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    init {
        updateProfileRes(0)
    }

    private fun getByteArrayFromResource(resId: Int): ByteArray {
        val bitmap = BitmapFactory.decodeResource(getApplication<Application>().resources, resId)
        val stream = ByteArrayOutputStream()
        // Use the PNG format
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    fun updateProfileRes(newIndex: Int) {
        val resId = PROFILE_RESOURCES[newIndex]
        val imageBytes = getByteArrayFromResource(resId)
        _uiState.value = _uiState.value.copy(
            profile = imageBytes,
            profileIndex = newIndex,
            errorMessage = null
        )
    }

    // List of indexes
    companion object {
        val PROFILE_RESOURCES = listOf(
            R.drawable.perfil1,
            R.drawable.perfil2,
            R.drawable.perfil3,
            R.drawable.perfil4,
            R.drawable.perfil5,
            R.drawable.perfil7
        )
    }

    // Clear update/delete states
    fun clearUpdateState() {
        _updateUiState.value = UpdateUiState()
    }

    fun clearDeleteState() {
        _deleteUiState.value = DeleteUiState()
    }

    // BACKEND

    // REGISTER
    fun registerNurse() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.name.isBlank() || state.surname.isBlank() || state.email.isBlank() ||
                state.username.isBlank() || state.password.isBlank()
            ) {
                _uiState.value =
                    state.copy(errorMessage = "Error: Todos los campos son obligatorios.")
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
                    profile = state.profile
                )
                val response = Connection.apiNurse.createNurse(newNurse)
                if (response.isSuccessful) {
                    _uiState.value =
                        state.copy(isRegistrationSuccessful = true, errorMessage = null)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API_ERROR", "Error response: $errorBody")
                    _uiState.value = state.copy(errorMessage = "Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "Error: ${e.message}")
                _uiState.value =
                    state.copy(errorMessage = "Connection error: ${e.localizedMessage}")
            }
        }
    }

    // LOGIN
    private val _loginEvent = Channel<Boolean>(Channel.BUFFERED)
    val loginEvent = _loginEvent.receiveAsFlow()
    var loggedNurse: Nurse by mutableStateOf(Nurse(null, "", "", "", "", "", null))

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
                Log.d(
                    "Failed: login",
                    "Failed connection to API ${e.message} ${e.printStackTrace()}"
                )
                false
            }
        }

    // LIST
    var nurseListUiState: NurseListUiState by mutableStateOf(NurseListUiState.Loading)
    fun getAll() {
        viewModelScope.launch {
            nurseListUiState = NurseListUiState.Loading
            try {
                val nurseList: List<Nurse> = Connection.apiNurse.getAll()
                nurseListUiState = NurseListUiState.Success(nurseList)
                Log.d("Succesful: list", "Succesful connection to API")
            } catch (e: Exception) {
                Log.d(
                    "Failed: list",
                    "Failed connection to API: ${e.message} ${e.printStackTrace()}"
                )
                nurseListUiState = NurseListUiState.Error
            }
        }
    }

    // GET BY NAME
    var nurseUiState: NurseUiState by mutableStateOf(NurseUiState.Loading)
    fun clearNurseUiState() {
        nurseUiState = NurseUiState.Loading
    }

    fun searchNurse(name: String) {
        viewModelScope.launch {
            nurseUiState = NurseUiState.Loading

            try {
                val nurse: Nurse = Connection.apiNurse.searchNurse(name)
                nurseUiState = NurseUiState.Success(nurse)
                Log.d("Succesful: get by name: searchNurse", "Succesful connection to API")
            } catch (e: Exception) {
                nurseUiState = NurseUiState.Error
                Log.d(
                    "Failed: get by name",
                    "Failed connection to API: ${e.message} ${e.printStackTrace()}"
                )
            }
        }
    }

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
        searchNurse(newQuery)
    }

    // GET BY USERNAME
    fun searchUser(username: String) {
        viewModelScope.launch {
            nurseUiState = NurseUiState.Loading

            try {
                val nurse: Nurse = Connection.apiNurse.searchUser(username)
                nurseUiState = NurseUiState.Success(nurse)
                Log.d("Succesful: search user", "Succesful connection to API")
            } catch (e: Exception) {
                nurseUiState = NurseUiState.Error
                Log.d(
                    "Failed: search user",
                    "Failed connection to API: ${e.message} ${e.printStackTrace()}"
                )
            }
        }
    }

    // GET BY ID
    fun getById(id: Long) {
        viewModelScope.launch {
            nurseUiState = NurseUiState.Loading

            try {
                val nurse: Nurse = Connection.apiNurse.getNurseById(id)
                nurseUiState = NurseUiState.Success(nurse)
                Log.d("Succesful: get by id", "Succesful connection to API")
            } catch (e: Exception) {
                nurseUiState = NurseUiState.Error
                Log.d(
                    "Failed: get by id",
                    "Failed connection to API: ${e.message} ${e.printStackTrace()}"
                )
            }
        }
    }

    // UPDATE PROFILE
    fun updateProfile(nurse: Nurse) {
        viewModelScope.launch {
            _updateUiState.value = UpdateUiState(isLoading = true)
            try {
                val id = nurse.id
                if (id == null) {
                    _updateUiState.value = UpdateUiState(
                        isLoading = false,
                        errorMessage = "Error: Nurse ID is null"
                    )
                    return@launch
                }

                // Validate required fields
                if (nurse.name.isBlank() || nurse.surname.isBlank() ||
                    nurse.user.isBlank() || nurse.password.isBlank()
                ) {
                    _updateUiState.value = UpdateUiState(
                        isLoading = false,
                        errorMessage = "Error: Name, surname, username and password are required"
                    )
                    return@launch
                }

                val response = Connection.apiNurse.updateNurse(id, nurse)
                if (response.isSuccessful) {
                    _updateUiState.value = UpdateUiState(isLoading = false, isSuccess = true)
                    // Refresh the nurse data
                    response.body()?.let { updatedNurse ->
                        nurseUiState = NurseUiState.Success(updatedNurse)
                        // Update loggedNurse if it's the same nurse
                        if (loggedNurse.id == id) {
                            loggedNurse = updatedNurse
                        }
                    }
                    Log.d("Succesful: update", "Nurse updated successfully")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API_ERROR", "Update error: $errorBody")
                    _updateUiState.value = UpdateUiState(
                        isLoading = false,
                        errorMessage = "Server error: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "Update error: ${e.message}")
                _updateUiState.value = UpdateUiState(
                    isLoading = false,
                    errorMessage = "Connection error: ${e.localizedMessage}"
                )
            }
        }
    }

    // DELETE NURSE
    fun deleteNurse(id: Long) {
        viewModelScope.launch {
            _deleteUiState.value = DeleteUiState(isLoading = true)
            try {
                val response = Connection.apiNurse.deleteNurse(id)
                if (response.isSuccessful) {
                    _deleteUiState.value = DeleteUiState(isLoading = false, isSuccess = true)
                    Log.d("Succesful: delete", "Nurse deleted successfully")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API_ERROR", "Delete error: $errorBody")
                    _deleteUiState.value = DeleteUiState(
                        isLoading = false,
                        errorMessage = "Server error: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "Delete error: ${e.message}")
                _deleteUiState.value = DeleteUiState(
                    isLoading = false,
                    errorMessage = "Connection error: ${e.localizedMessage}"
                )
            }
        }
    }


    fun logout() {
        loggedNurse = Nurse(null, "", "", "", "", "", null)
        clearNurseUiState()
        viewModelScope.launch {
            _loginEvent.send(false)
        }
    }

}