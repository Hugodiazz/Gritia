package com.devdiaz.gritia.data.repository

import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val sessionStatus: StateFlow<SessionStatus>
    val currentUser: UserInfo?

    suspend fun signInWithGoogle(idToken: String, rawNonce: String? = null)
    suspend fun signOut()
    suspend fun deleteAccount()
    fun isUserLoggedIn(): Boolean
}

sealed class SessionStatus {
    data object Loading : SessionStatus()
    data class Authenticated(val user: UserInfo) : SessionStatus()
    data object Unauthenticated : SessionStatus()
    data class Error(val message: String) : SessionStatus()
}
