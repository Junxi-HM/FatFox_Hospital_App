package com.example.fatfoxhospital.viewmodel.uistate

import com.example.fatfoxhospital.model.Nurse

sealed interface NurseUiState {
    data class Success(val nurse: Nurse) : NurseUiState
    object Error : NurseUiState
    object Loading : NurseUiState
}
