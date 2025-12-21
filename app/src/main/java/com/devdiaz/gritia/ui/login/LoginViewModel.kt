package com.devdiaz.gritia.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devdiaz.gritia.data.repository.UserRepository
import com.devdiaz.gritia.model.Gender
import com.devdiaz.gritia.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onLoginClick(onLoginSuccess: () -> Unit) {
        viewModelScope.launch {
            // Create a test user if one doesn't exist or just update the current one
            // specific to this dev phase
            val emailInput = _email.value.ifEmpty { "usuario@prueba.com" }

            // Check if we already have this user?
            // For simplicity in this "Test User" request, we just save a new one or update logic.
            // Since getUser returns a Flow, collecting it here might be tricky if we just want a
            // one-shot check.
            // Let's just blindly save a user for testing purposes so data exists.

            val testUser =
                User(
                    id = 0, // 0 lets Room auto-generate ID, or we can look up by email
                    // later
                    name = "Hugo Zaid",
                    email = emailInput,
                    gender = Gender.MALE,
                    birthDate = LocalDate.of(2002, 5, 16),
                    height = 173f,
                    currentWeight = 80.1f
                )

            withContext(Dispatchers.IO) { userRepository.saveUser(testUser) }

            // Navigate
            withContext(Dispatchers.Main) { onLoginSuccess() }
        }
    }
}
