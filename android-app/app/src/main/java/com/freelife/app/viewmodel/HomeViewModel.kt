package com.freelife.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.freelife.app.model.GroupResponse
import com.freelife.app.model.UiState
import com.freelife.app.repository.GroupRepository
import com.freelife.app.repository.TokenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val groupRepository = GroupRepository(application)
    private val tokenRepository = TokenRepository(application)

    private val _groupsState = MutableStateFlow<UiState<List<GroupResponse>>>(UiState.Idle)
    val groupsState: StateFlow<UiState<List<GroupResponse>>> = _groupsState.asStateFlow()

    private val _createGroupState = MutableStateFlow<UiState<GroupResponse>>(UiState.Idle)
    val createGroupState: StateFlow<UiState<GroupResponse>> = _createGroupState.asStateFlow()

    private val _joinGroupState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val joinGroupState: StateFlow<UiState<Unit>> = _joinGroupState.asStateFlow()

    fun userName(): String = tokenRepository.getUserName()

    fun loadGroups() {
        viewModelScope.launch {
            _groupsState.value = UiState.Loading
            val result = groupRepository.getGroups()
            _groupsState.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Unable to load groups.") }
            )
        }
    }

    fun createGroup(name: String) {
        viewModelScope.launch {
            _createGroupState.value = UiState.Loading
            val result = groupRepository.createGroup(name)
            _createGroupState.value = result.fold(
                onSuccess = {
                    loadGroups()
                    UiState.Success(it)
                },
                onFailure = { UiState.Error(it.message ?: "Unable to create group.") }
            )
        }
    }

    fun joinGroup(inviteCode: String) {
        viewModelScope.launch {
            _joinGroupState.value = UiState.Loading
            val result = groupRepository.joinGroup(inviteCode)
            _joinGroupState.value = result.fold(
                onSuccess = {
                    loadGroups()
                    UiState.Success(Unit)
                },
                onFailure = { UiState.Error(it.message ?: "Unable to join group.") }
            )
        }
    }

    fun resetCreateGroupState() {
        _createGroupState.value = UiState.Idle
    }

    fun resetJoinGroupState() {
        _joinGroupState.value = UiState.Idle
    }
}
