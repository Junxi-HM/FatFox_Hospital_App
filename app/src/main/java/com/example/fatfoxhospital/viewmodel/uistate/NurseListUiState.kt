package com.example.fatfoxhospital.viewmodel.uistate

import com.example.fatfoxhospital.model.Nurse

sealed interface NurseListUiState {
    data class Success(val nurseList: List<Nurse>) : NurseListUiState
    object Error : NurseListUiState
    object Loading : NurseListUiState
}
