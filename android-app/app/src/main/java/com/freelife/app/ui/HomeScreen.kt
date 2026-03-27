package com.freelife.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.freelife.app.model.GroupResponse
import com.freelife.app.model.UiState
import com.freelife.app.viewmodel.HomeViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val groupsState by viewModel.groupsState.collectAsState()
    val createGroupState by viewModel.createGroupState.collectAsState()
    val joinGroupState by viewModel.joinGroupState.collectAsState()
    var showCreateGroupDialog by rememberSaveable { mutableStateOf(false) }
    var showJoinGroupDialog by rememberSaveable { mutableStateOf(false) }
    var groupName by rememberSaveable { mutableStateOf("") }
    var inviteCode by rememberSaveable { mutableStateOf("") }
    var localDialogError by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadGroups()
    }

    LaunchedEffect(createGroupState) {
        if (createGroupState is UiState.Success) {
            showCreateGroupDialog = false
            groupName = ""
            localDialogError = null
            viewModel.resetCreateGroupState()
        }
    }

    LaunchedEffect(joinGroupState) {
        if (joinGroupState is UiState.Success) {
            showJoinGroupDialog = false
            inviteCode = ""
            localDialogError = null
            viewModel.resetJoinGroupState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FreeLife") },
                actions = {
                    IconButton(onClick = {
                        localDialogError = null
                        showJoinGroupDialog = true
                    }) {
                        Icon(Icons.Filled.PersonAdd, contentDescription = "Join Group")
                    }
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    localDialogError = null
                    showCreateGroupDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Create Group"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val userName = viewModel.userName()
            if (userName.isNotBlank()) {
                Text(
                    text = "Welcome, $userName",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            when (groupsState) {
                UiState.Idle, UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = (groupsState as UiState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Button(onClick = viewModel::loadGroups) {
                                Text("Retry")
                            }
                        }
                    }
                }

                is UiState.Success -> {
                    val groups = (groupsState as UiState.Success<List<GroupResponse>>).data
                    if (groups.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No groups yet. Tap + to create your first one.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(groups, key = { it.id }) { group ->
                                GroupCard(
                                    group = group,
                                    onDetails = {
                                        navController.navigate(Screen.Group.createRoute(group.id))
                                    },
                                    onViewMap = {
                                        navController.navigate(Screen.Map.createRoute(group.id))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateGroupDialog) {
        val remoteError = (createGroupState as? UiState.Error)?.message
        val dialogError = localDialogError
        AlertDialog(
            onDismissRequest = {
                showCreateGroupDialog = false
                localDialogError = null
                viewModel.resetCreateGroupState()
            },
            title = { Text("Create Group") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = groupName,
                        onValueChange = {
                            groupName = it
                            localDialogError = null
                        },
                        label = { Text("Group Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    dialogError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    } ?: remoteError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (groupName.isBlank()) {
                            localDialogError = "Group name is required."
                            return@Button
                        }
                        localDialogError = null
                        viewModel.createGroup(groupName)
                    },
                    enabled = createGroupState !is UiState.Loading
                ) {
                    if (createGroupState is UiState.Loading) {
                        CircularProgressIndicator(strokeWidth = 2.dp)
                    } else {
                        Text("Create")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCreateGroupDialog = false
                        localDialogError = null
                        viewModel.resetCreateGroupState()
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showJoinGroupDialog) {
        val remoteError = (joinGroupState as? UiState.Error)?.message
        AlertDialog(
            onDismissRequest = {
                showJoinGroupDialog = false
                localDialogError = null
                viewModel.resetJoinGroupState()
            },
            title = { Text("Join Group") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = inviteCode,
                        onValueChange = {
                            inviteCode = it.uppercase()
                            localDialogError = null
                        },
                        label = { Text("Invite Code") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    (localDialogError ?: remoteError)?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inviteCode.isBlank()) {
                            localDialogError = "Invite code is required."
                            return@Button
                        }
                        localDialogError = null
                        viewModel.joinGroup(inviteCode)
                    },
                    enabled = joinGroupState !is UiState.Loading
                ) {
                    if (joinGroupState is UiState.Loading) CircularProgressIndicator(strokeWidth = 2.dp)
                    else Text("Join")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showJoinGroupDialog = false
                    localDialogError = null
                    viewModel.resetJoinGroupState()
                }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun GroupCard(
    group: GroupResponse,
    onDetails: () -> Unit,
    onViewMap: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Invite: ${group.inviteCode} · Members: ${group.memberCount}",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onViewMap, modifier = Modifier.weight(1f)) {
                    Text("Map")
                }
                androidx.compose.material3.OutlinedButton(
                    onClick = onDetails,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Details")
                }
            }
        }
    }
}
