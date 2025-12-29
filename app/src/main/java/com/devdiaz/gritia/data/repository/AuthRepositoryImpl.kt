package com.devdiaz.gritia.data.repository

import android.util.Log
import com.devdiaz.gritia.data.repository.SessionStatus.*
import com.devdiaz.gritia.model.dto.UserDto
import com.devdiaz.gritia.model.dto.toDomain
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus as SupabaseSessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@Singleton
class AuthRepositoryImpl
@Inject
constructor(
        private val auth: Auth,
        private val userRepository: UserRepository,
        private val postgrest: Postgrest
) : AuthRepository {

    private val _sessionStatus = MutableStateFlow<SessionStatus>(SessionStatus.Loading)
    override val sessionStatus: StateFlow<SessionStatus> = _sessionStatus.asStateFlow()

    override val currentUser: UserInfo?
        get() = auth.currentUserOrNull()

    init {
        // Observe Supabase session changes
        CoroutineScope(Dispatchers.IO).launch {
            auth.sessionStatus.collect { status ->
                android.util.Log.d("AuthRepository", "Supabase session status changed: $status")
                when (status) {
                    is SupabaseSessionStatus.Authenticated -> {
                        auth.currentUserOrNull()?.let { userInfo ->
                            android.util.Log.d(
                                    "AuthRepository",
                                    "User authenticated: ${userInfo.email}"
                            )
                            _sessionStatus.value = Authenticated(userInfo)
                            syncUser(userInfo)
                        }
                    }
                    is SupabaseSessionStatus.NotAuthenticated -> {
                        android.util.Log.d("AuthRepository", "User not authenticated")
                        _sessionStatus.value = SessionStatus.Unauthenticated
                    }
                    is SupabaseSessionStatus.Initializing -> {
                        _sessionStatus.value = SessionStatus.Loading
                    }
                    // Ignore other statuses for now
                    else -> {}
                }
            }
        }
    }

    private suspend fun syncUser(userInfo: UserInfo) {
        val email = userInfo.email
        android.util.Log.d("AuthRepository", "syncUser called for email: $email")

        if (email == null) {
            android.util.Log.e("AuthRepository", "Email is null, cannot sync user")
            return
        }

        try {
            // Check if user exists in Supabase public.users
            android.util.Log.d("AuthRepository", "Querying Supabase for user with email: $email")

            // Note: Ensure RLS policies on 'users' table allow SELECT for authenticated users
            val result =
                    postgrest
                            .from("users")
                            .select { filter { eq("email", email) } }
                            .decodeSingleOrNull<UserDto>()

            android.util.Log.d("AuthRepository", "Supabase query result: $result")

            if (result != null) {
                // User exists in Supabase, sync to local DB
                val domainUser = result.toDomain()
                android.util.Log.d(
                        "AuthRepository",
                        "User found in Supabase. Syncing to local DB: $domainUser"
                )

                val localUser = userRepository.getUserByEmail(email)
                val userToSave = domainUser.copy(id = localUser?.id ?: 0)

                userRepository.saveUser(userToSave)
                android.util.Log.d("AuthRepository", "User saved to local DB (Update)")
            } else {
                // User does NOT exist in Supabase (First login via Google)
                android.util.Log.d(
                        "AuthRepository",
                        "User not found in Supabase. Creating new user."
                )

                // Extract name safely from JsonObject
                // Note: userMetadata is Map<String, JsonElement>.
                // We use toString().trim('"') as a simple workaround if casting fails,
                // or default to "Usuario"
                val rawName =
                        userInfo.userMetadata?.get("full_name")?.toString()?.trim('"')
                                ?: userInfo.userMetadata?.get("name")?.toString()?.trim('"')
                                        ?: "Usuario"

                val newUser =
                        UserDto(
                                id = userInfo.id, // We MUST use the Auth UUID so the profile
                                // links to the account
                                name = rawName,
                                email = email,
                                gender = "Otro", // Default
                                birthDate = null,
                                height = null,
                                currentWeight = null,
                                createdAt = Clock.System.now().toString()
                        )

                // Optimistic Local Save
                try {
                    userRepository.saveUser(newUser.toDomain())
                    android.util.Log.d("AuthRepository", "New user saved to local DB (Optimistic)")
                } catch (e: Exception) {
                    android.util.Log.e("AuthRepository", "Failed to save locally", e)
                }

                // Insert into Supabase
                try {
                    android.util.Log.d(
                            "AuthRepository",
                            "Inserting new user into Supabase: $newUser"
                    )
                    postgrest.from("users").insert(newUser)
                    android.util.Log.d("AuthRepository", "User inserted into Supabase successfully")
                } catch (e: Exception) {
                    android.util.Log.e(
                            "AuthRepository",
                            "Failed to insert into Supabase (Check RLS Policies!)",
                            e
                    )
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Error syncing user", e)
            e.printStackTrace()
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
        }
    }

    override suspend fun signOut() {
        android.util.Log.d("AuthRepository", "signOut() called")
        auth.signOut()
        _sessionStatus.value = SessionStatus.Unauthenticated
        android.util.Log.d("AuthRepository", "signOut() completed, status set to Unauthenticated")
    }

    override suspend fun deleteAccount() {

        val currentUser = auth.currentUserOrNull()
        if (currentUser == null) {
            signOut()
            return
        }
        val userId = currentUser.id
        android.util.Log.d("AuthRepository", "Deleting user account for id:$userId")
        try {
            val response = postgrest.from("users").delete { filter { eq("id", userId) } }
            android.util.Log.d("AuthRepository", "User deletion response from Supabase: $response")
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Error deleting user from Supabase", e)
        }
        userRepository.clearUserData()
        signOut()
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentSessionOrNull() != null
    }
}
