package com.freelife.app.repository

import android.content.Context
import com.freelife.app.model.CreateGroupRequest
import com.freelife.app.model.GroupResponse
import com.freelife.app.model.JoinGroupRequest
import com.freelife.app.model.MemberResponse
import com.freelife.app.network.RetrofitClient
import retrofit2.Response

class GroupRepository(context: Context) {
    private val api = RetrofitClient.instance
    private val tokenRepository = TokenRepository(context)

    suspend fun getGroups(): Result<List<GroupResponse>> {
        return runCatching {
            api.getGroups(requireBearerToken())
                .requireBody("Failed to load groups")
        }
    }

    suspend fun createGroup(name: String): Result<GroupResponse> {
        return runCatching {
            api.createGroup(requireBearerToken(), CreateGroupRequest(name.trim()))
                .requireBody("Failed to create group")
        }
    }

    suspend fun joinGroup(inviteCode: String): Result<Unit> {
        return runCatching {
            val response = api.joinGroup(
                requireBearerToken(),
                JoinGroupRequest(inviteCode.trim())
            )
            response.requireUnit("Failed to join group")
        }
    }

    suspend fun leaveGroup(groupId: Int): Result<Unit> {
        return runCatching {
            api.leaveGroup(requireBearerToken(), groupId)
                .requireUnit("Failed to leave group")
        }
    }

    suspend fun getGroupMembers(groupId: Int): Result<List<MemberResponse>> {
        return runCatching {
            api.getGroupMembers(requireBearerToken(), groupId)
                .requireBody("Failed to load group members")
        }
    }

    private fun requireBearerToken(): String {
        return tokenRepository.getBearerToken()
            ?: throw IllegalStateException("You are not logged in.")
    }

    private fun <T> Response<T>.requireBody(defaultMessage: String): T {
        if (isSuccessful) {
            return body() ?: throw IllegalStateException("$defaultMessage: empty response body.")
        }

        val message = errorBody()?.string()?.trim().takeUnless { it.isNullOrBlank() }
            ?: message().takeUnless { it.isBlank() }
            ?: defaultMessage

        throw IllegalStateException(message)
    }

    private fun Response<Unit>.requireUnit(defaultMessage: String) {
        if (isSuccessful) {
            return
        }

        val message = errorBody()?.string()?.trim().takeUnless { it.isNullOrBlank() }
            ?: message().takeUnless { it.isBlank() }
            ?: defaultMessage

        throw IllegalStateException(message)
    }
}
