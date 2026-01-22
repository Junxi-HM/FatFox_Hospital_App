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
    val profile: ByteArray? = null, // 存储 LONGBLOB 数据
    val profileIndex: Int = 0,      // 记录当前选中的资源索引
    val errorMessage: String? = null,
    val isRegistrationSuccessful: Boolean = false
)

class NurseViewModel(application: Application) : AndroidViewModel(application) {

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery
    private val _selectedNurse = MutableLiveData<Nurse?>()
    val selectedNurse: LiveData<Nurse?> = _selectedNurse
    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()




    // DETAIL
    fun selectNurse(nurse: Nurse) {
        _selectedNurse.value = nurse
    }

    fun clearSelectedNurse() {
        _selectedNurse.value = null
    }

    // LOGIN
    private val _loginEvent = Channel<Boolean>(Channel.BUFFERED)
    val loginEvent = _loginEvent.receiveAsFlow()
    fun login(username: String, password: String) {
        viewModelScope.launch {
            val ok = loginAuthenticate(username, password)
            _loginEvent.send(ok)   // true 成功，false 失败
        }
    }
    private suspend fun loginAuthenticate(username: String, password: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                Connection.apiNurse.login(LoginRequest(username, password))
                Connection.apiNurse.login(LoginRequest(username, password))
                    .isSuccessful
            } catch (e: Exception) {
                false
            }
        }


    // REGISTRATION
    fun updateName(newName: String) { _uiState.value = _uiState.value.copy(name = newName) }
    fun updateSurname(newSurname: String) { _uiState.value = _uiState.value.copy(surname = newSurname) }
    fun updateEmail(newEmail: String) { _uiState.value = _uiState.value.copy(email = newEmail.trim()) }
    fun updateUsername(newUsername: String) { _uiState.value = _uiState.value.copy(username = newUsername.trim()) }
    fun updatePassword(newPassword: String) { _uiState.value = _uiState.value.copy(password = newPassword) }
    fun registrationComplete() { _uiState.value = RegistrationUiState() }
    fun clearErrorMessage() { _uiState.value = _uiState.value.copy(errorMessage = null) }

    init {
        updateProfileRes(0)
    }
    private fun getByteArrayFromResource(resId: Int): ByteArray {
        val bitmap = BitmapFactory.decodeResource(getApplication<Application>().resources, resId)
        val stream = ByteArrayOutputStream()
        // Utilice el formato PNG
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
    fun registerNurse() {
        viewModelScope.launch {
            val state = _uiState.value
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
                    profile = state.profile
                )
                val response = Connection.apiNurse.createNurse(newNurse)
                if (response.isSuccessful) {
                    _uiState.value = state.copy(isRegistrationSuccessful = true, errorMessage = null)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API_ERROR", "Error response: $errorBody")
                    _uiState.value = state.copy(errorMessage = "Error del servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "Error: ${e.message}")
                _uiState.value = state.copy(errorMessage = "Error de conexión: ${e.localizedMessage}")
            }
        }
    }

    // Lista de índices
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

    // List
    var nurseListUiState: NurseListUiState by mutableStateOf(NurseListUiState.Loading);
    fun getAll() {
        viewModelScope.launch {
            nurseListUiState = NurseListUiState.Loading
            try {
                val nurseList: List<Nurse> = Connection.apiNurse.getAll()
                nurseListUiState = NurseListUiState.Success(nurseList)
                Log.d("Successful", "Successful connection to API")
            } catch (e: Exception) {
                Log.e("Failed", "API Connection Error: ${e.message}", e)
                val errorMessage = e.localizedMessage ?: "Unknown connection error"
                nurseListUiState = NurseListUiState.Error(errorMessage)
            }
        }
    }

    // Search/Get by name
    var nurseUiState: NurseUiState by mutableStateOf(NurseUiState.Loading);
    fun searchNurse(name: String){
        viewModelScope.launch {
            nurseUiState = NurseUiState.Loading

            try {
                val nurse: Nurse = Connection.apiNurse.searchNurse(name)
                nurseUiState = NurseUiState.Success(nurse)
                Log.d("Succesful","Succesful connection to API")
            }catch (e: Exception){
                Log.d("Failed", "Failed connection to API: ${e.message} ${e.printStackTrace()}")
                nurseUiState = NurseUiState.Error
            }
        }
    }
    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
        searchNurse(newQuery)
    }
}