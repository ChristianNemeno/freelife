package com.freelife.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.freelife.app.repository.TokenRepository

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenRepository = TokenRepository(application)

    fun getUserName(): String = tokenRepository.getUserName()

    fun getUserEmail(): String = tokenRepository.getUserEmail()

    fun logout() = tokenRepository.logout()
}
