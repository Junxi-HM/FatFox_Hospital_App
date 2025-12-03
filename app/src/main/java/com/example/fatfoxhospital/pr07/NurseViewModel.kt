package com.example.fatfoxhospital.pr07

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estado de la UI para mejor manejo de campos
data class RegistrationUiState(
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val errorMessage: String? = null,
    val isRegistrationSuccessful: Boolean = false
)

class NurseRegistrationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    // --- Funciones de Actualización ---

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

    // --- Lógica de Validación ---

    private fun isEmailValid(email: String): Boolean {
        // Comprobar si contiene '@' y tiene un formato básico
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Lógica principal de registro
    fun registerNurse() {
        viewModelScope.launch {
            val state = _uiState.value

            // 1. Validación de campos obligatorios
            if (state.name.isBlank() || state.surname.isBlank() || state.email.isBlank() || state.username.isBlank() || state.password.isBlank()) {
                _uiState.value = state.copy(errorMessage = "Error: Todos los campos son obligatorios.")
                return@launch
            }

            // 2. Validación de formato de correo electrónico
            if (!isEmailValid(state.email)) {
                _uiState.value = state.copy(errorMessage = "Error: Formato de correo electrónico inválido. Debe contener '@'.")
                return@launch
            }

            // 3. Comprobación de duplicidad de Correo Electrónico
            if (FAKE_REGISTERED_NURSES.any { it.email.equals(state.email, ignoreCase = true) }) {
                _uiState.value = state.copy(errorMessage = "Error: Dirección de correo electrónico ya registrada.")
                return@launch
            }

            // 4. Comprobación de duplicidad de Nombre de Usuario
            if (FAKE_REGISTERED_NURSES.any { it.user.equals(state.username, ignoreCase = true) }) {
                _uiState.value = state.copy(errorMessage = "Error: El usuario ya existe.")
                return@launch
            }

            // 5. Simulación de Registro Exitoso
            val newNurse = Nurse(
                id = System.currentTimeMillis(),
                name = state.name,
                surname = state.surname,
                email = state.email, // Correo
                user = state.username,
                password = state.password
            )

            // Simulación de éxito. Aquí iría la llamada al Backend.
            // Para el propósito de la actividad, marcamos el éxito.
            _uiState.value = RegistrationUiState(
                isRegistrationSuccessful = true
            )
        }
    }

    // Reinicia el estado de éxito (necesario si el usuario vuelve a esta pantalla)
    fun registrationComplete() {
        _uiState.value = RegistrationUiState()
    }

    // Función para limpiar solo el mensaje de error
    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}