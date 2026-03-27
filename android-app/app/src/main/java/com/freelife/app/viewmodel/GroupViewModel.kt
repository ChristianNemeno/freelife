package com.freelife.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.freelife.app.model.GroupResponse
import com.freelife.app.model.MemberResponse
import com.freelife.app.model.UiState
import com.freelife.app.repository.GroupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupViewModel(application: Application) : AndroidViewModel(application) {

    private val groupRepository = GroupRepository(application)

    private val _group = MutableStateFlow<GroupResponse?>(null)
    val group: StateFlow<GroupResponse?> = _group.asStateFlow()

    private val _membersState = MutableStateFlow<UiState<List<MemberResponse>>>(UiState.Idle)
    val membersState: StateFlow<UiState<List<MemberResponse>>> = _membersState.asStateFlow()

    private val _leaveState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val leaveState: StateFlow<UiState<Unit>> = _leaveState.asStateFlow()

    fun load(groupId: Int) {
        viewModelScope.launch {
            // Load group info and members concurrently
            launch {
                groupRepository.getGroups().onSuccess { groups ->
                    _group.value = groups.find { it.id == groupId }
                }
            }
            launch {
                _membersState.value = UiState.Loading
                val result = groupRepository.getGroupMembers(groupId)
                _membersState.value = result.fold(
                    onSuccess = { UiState.Success(it) },
                    onFailure = { UiState.Error(it.message ?: "Unable to load members.") }
                )
            }
        }
    }

    fun leaveGroup(groupId: Int) {
        viewModelScope.launch {
            _leaveState.value = UiState.Loading
            val result = groupRepository.leaveGroup(groupId)
            _leaveState.value = result.fold(
                onSuccess = { UiState.Success(Unit) },
                onFailure = { UiState.Error(it.message ?: "Unable to leave group.") }
            )
        }
    }
}
