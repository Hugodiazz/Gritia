package com.devdiaz.gritia.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdiaz.gritia.data.repository.AuthRepository
import com.devdiaz.gritia.data.repository.SessionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    val sessionStatus: StateFlow<SessionStatus> =
            authRepository.sessionStatus.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = SessionStatus.Loading
            )

    fun signInWithGoogle(idToken: String, rawNonce: String? = null) {
        viewModelScope.launch { authRepository.signInWithGoogle(idToken, rawNonce) }
    }

    fun signOut() {
        viewModelScope.launch { authRepository.signOut() }
    }
}
