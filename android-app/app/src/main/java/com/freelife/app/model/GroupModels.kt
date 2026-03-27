package com.freelife.app.model

data class GroupResponse(
    val id: Int,
    val name: String,
    val inviteCode: String,
    val memberCount: Int
)

data class CreateGroupRequest(
    val name: String
)

data class JoinGroupRequest(
    val inviteCode: String
)

data class MemberResponse(
    val userId: Int,
    val name: String,
    val email: String
)
