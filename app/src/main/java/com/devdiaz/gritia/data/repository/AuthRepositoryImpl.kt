package com.devdiaz.gritia.data.repository

import com.devdiaz.gritia.data.repository.SessionStatus.*
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus as SupabaseSessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Singleton
class AuthRepositoryImpl
@Inject
constructor(private val auth: Auth, private val userRepository: UserRepository) : AuthRepository {

    private val _sessionStatus = MutableStateFlow<SessionStatus>(SessionStatus.Loading)
    override val sessionStatus: StateFlow<SessionStatus> = _sessionStatus.asStateFlow()

    override val currentUser: UserInfo?
        get() = auth.currentUserOrNull()

    init {
        // Observe Supabase session changes
        // running in a scope is tricky here without an injected scope, but usually Repositories
        // shouldn't launch
        // For now, we'll check initial state and rely on explicit calls or reactive streams from
        // Supabase if available
        // Supabase Auth has 'sessionStatus' flow
        CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            auth.sessionStatus.collect { status ->
                when (status) {
                    is SupabaseSessionStatus.Authenticated -> {
                        auth.currentUserOrNull()?.let { userInfo ->
                            _sessionStatus.value = Authenticated(userInfo)

                            // Sync user to local database
                            userInfo.email?.let { email ->
                                val localUser = userRepository.getUserByEmail(email)
                                if (localUser == null) {
                                    val newUser =
                                            com.devdiaz.gritia.model.User(
                                                    id = 0,
                                                    name = userInfo.userMetadata?.get("name") as? String
                                                                    ?: "Usuario",
                                                    email = email,
                                                    gender = com.devdiaz.gritia.model.Gender.OTHER,
                                                    birthDate = null,
                                                    height = null,
                                                    currentWeight = null
                                            )
                                    userRepository.saveUser(newUser)
                                }
                            }
                        }
                    }
                    is SupabaseSessionStatus.NotAuthenticated -> {
                        _sessionStatus.value = SessionStatus.Unauthenticated
                    }
                    is SupabaseSessionStatus.Initializing -> {
                        _sessionStatus.value = SessionStatus.Loading
                    }
                    //                    is SupabaseSessionStatus.NetworkError -> {
                    //                        // Keep previous state or show error? For now, ignore
                    // network error in
                    //                        // status flow
                    //                        // usually it means we couldn't refresh token
                    //                    }
                    is SupabaseSessionStatus.RefreshFailure -> TODO()
                }
            }
        }
    }

    override suspend fun signInWithGoogle(idToken: String, rawNonce: String?) {
        try {
            auth.signInWith(IDToken) {
                this.idToken = idToken
                this.provider = Google
                this.nonce = rawNonce
            }
            // The sessionStatus flow will update automatically
        } catch (e: Exception) {
            _sessionStatus.value = SessionStatus.Error(e.message ?: "Login failed")
            // Reset to unauthenticated if failed?
            // Depending on error.
        }
    }

    override suspend fun signOut() {
        auth.signOut()
        _sessionStatus.value = SessionStatus.Unauthenticated
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentSessionOrNull() != null
    }
}
