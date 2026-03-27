package com.freelife.app.repository

import android.content.Context
import com.freelife.app.model.AuthResponse
import com.freelife.app.model.LoginRequest
import com.freelife.app.model.RegisterRequest
import com.freelife.app.network.RetrofitClient
import retrofit2.Response

class AuthRepository(context: Context) {
    private val api = RetrofitClient.instance
    private val tokenRepository = TokenRepository(context)

    suspend fun register(name: String, email: String, password: String): Result<AuthResponse> {
        return runCatching {
            val response = api.register(
                RegisterRequest(
                    name = name.trim(),
                    email = email.trim(),
                    password = password
                )
            )
            response.requireBody("Registration failed")
                .also(::persistSession)
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return runCatching {
            val response = api.login(
                LoginRequest(
                    email = email.trim(),
                    password = password
                )
            )
            response.requireBody("Login failed")
                .also(::persistSession)
        }
    }

    private fun persistSession(authResponse: AuthResponse) {
        tokenRepository.saveToken(authResponse.token)
        tokenRepository.saveUser(authResponse.userId, authResponse.name, authResponse.email)
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
}
