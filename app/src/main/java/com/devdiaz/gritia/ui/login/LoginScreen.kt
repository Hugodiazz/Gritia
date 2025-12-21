package com.devdiaz.gritia.ui.login

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.devdiaz.gritia.BuildConfig
import com.devdiaz.gritia.data.repository.SessionStatus
import com.devdiaz.gritia.ui.auth.AuthViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
        onLoginSuccess: () -> Unit,
        viewModel: AuthViewModel = hiltViewModel() // Use AuthViewModel
) {
        // We can keep local state for email/password if we want to preserve the UI fields
        // even if they don't do Supabase auth yet (or we can wire them later).
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }

        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val sessionStatus by viewModel.sessionStatus.collectAsState()

        // Handle authentication state changes
        LaunchedEffect(sessionStatus) {
                if (sessionStatus is SessionStatus.Authenticated) {
                        onLoginSuccess()
                }
        }

        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                // Hero Image & Header Section
                Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                        AsyncImage(
                                model =
                                        "https://lh3.googleusercontent.com/aida-public/AB6AXuCo0kC0nFGnC_QoI0FQ0QQvPr1fqMAeIItGNr9tiegggakG225C2DC2x2Qodbc1KLgmC7VtNvNRo6px3bfRks1hl_DoZuRscHzsWsLAzSD2FExosidOI2FECWOlwCo0GownutfIScuVD8MXplWTS94JG0k0Vpx_8d2x18fGJBHMCXuyG1cKcuYHvkSmDWdO-X7e-yxR13ksm8Ltpcr6kdylA0Z6N1FYYMOTg1XqsZCgb0niYM1tDGMeEv4S76oawBcBWJhPnpL2IfE",
                                contentDescription = "Gym Background",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                        )
                        // Gradient Overlay
                        Box(
                                modifier =
                                        Modifier.fillMaxSize()
                                                .background(
                                                        Brush.verticalGradient(
                                                                colors =
                                                                        listOf(
                                                                                Color.Black.copy(
                                                                                        alpha = 0.3f
                                                                                ),
                                                                                Color.Transparent,
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .background
                                                                        )
                                                        )
                                                )
                        )

                        // Logo/Branding Overlay
                        Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                                Text(
                                        text = "Bienvenido",
                                        style =
                                                MaterialTheme.typography.headlineMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onBackground
                                                )
                                )
                                Text(
                                        text = "Alcanza tus metas con Gritia",
                                        style =
                                                MaterialTheme.typography.bodyMedium.copy(
                                                        // simple fallback if TextSecondaryLight
                                                        // undefined
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                )
                                )
                        }
                }

                // Main Content Area
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(top = 300.dp)
                                        .padding(horizontal = 24.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        // Email Input
                        OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                placeholder = { Text("Email") },
                                leadingIcon = {
                                        Icon(
                                                Icons.Default.Email,
                                                contentDescription = null,
                                                tint = Color.Gray
                                        )
                                },
                                shape = RoundedCornerShape(28.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors =
                                        OutlinedTextFieldDefaults.colors(
                                                unfocusedContainerColor =
                                                        MaterialTheme.colorScheme.surface,
                                                focusedContainerColor =
                                                        MaterialTheme.colorScheme.surface,
                                                unfocusedBorderColor =
                                                        MaterialTheme.colorScheme.outline.copy(
                                                                alpha = 0.1f
                                                        ),
                                                focusedBorderColor =
                                                        MaterialTheme.colorScheme.primary
                                        )
                        )

                        // Password Input
                        OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                placeholder = { Text("Contraseña") },
                                leadingIcon = {
                                        Icon(
                                                Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = Color.Gray
                                        )
                                },
                                trailingIcon = {
                                        IconButton(
                                                onClick = { passwordVisible = !passwordVisible }
                                        ) {
                                                Icon(
                                                        if (passwordVisible)
                                                                Icons.Default.Visibility
                                                        else Icons.Default.VisibilityOff,
                                                        contentDescription = null,
                                                        tint = Color.Gray
                                                )
                                        }
                                },
                                visualTransformation =
                                        if (passwordVisible) VisualTransformation.None
                                        else PasswordVisualTransformation(),
                                keyboardOptions =
                                        KeyboardOptions(keyboardType = KeyboardType.Password),
                                shape = RoundedCornerShape(28.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors =
                                        OutlinedTextFieldDefaults.colors(
                                                unfocusedContainerColor =
                                                        MaterialTheme.colorScheme.surface,
                                                focusedContainerColor =
                                                        MaterialTheme.colorScheme.surface,
                                                unfocusedBorderColor =
                                                        MaterialTheme.colorScheme.outline.copy(
                                                                alpha = 0.1f
                                                        ),
                                                focusedBorderColor =
                                                        MaterialTheme.colorScheme.primary
                                        )
                        )

                        // Forgot Password
                        Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterEnd
                        ) {
                                TextButton(onClick = { /* TODO */}) {
                                        Text(
                                                "Olvidaste tu contraseña?",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                        )
                                }
                        }

                        // Login Button
                        Button(
                                onClick = { /* TODO: Implement email/password login via AuthViewModel if desired */
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = Color(0xFF111814)
                                        ),
                                shape = RoundedCornerShape(28.dp)
                        ) { Text("Iniciar sesión", fontWeight = FontWeight.Bold, fontSize = 16.sp) }

                        // Divider
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 16.dp)
                        ) {
                                HorizontalDivider(modifier = Modifier.weight(1f))
                                Text(
                                        "O continúa con",
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                )
                                HorizontalDivider(modifier = Modifier.weight(1f))
                        }

                        // Social Buttons (Google)
                        OutlinedButton(
                                onClick = { scope.launch { signInWithGoogle(context, viewModel) } },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(28.dp),
                                colors =
                                        ButtonDefaults.outlinedButtonColors(
                                                containerColor = MaterialTheme.colorScheme.surface,
                                                contentColor = MaterialTheme.colorScheme.onSurface
                                        ),
                                border = null
                        ) {
                                if (sessionStatus is SessionStatus.Loading) {
                                        CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                strokeWidth = 2.dp
                                        )
                                } else {
                                        Text("Continuar con Google")
                                }
                        }

                        if (sessionStatus is SessionStatus.Error) {
                                Text(
                                        text = (sessionStatus as SessionStatus.Error).message,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Footer
                        Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                Row {
                                        Text("No tienes una cuenta? ", color = Color.Gray)
                                        Text(
                                                "Registrate",
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                        )
                                }
                        }
                }
        }
}

private suspend fun signInWithGoogle(context: android.content.Context, viewModel: AuthViewModel) {
        try {
                val credentialManager = CredentialManager.create(context)

                // Generate a random nonce
                val rawNonce = java.util.UUID.randomUUID().toString()
                val bytes = rawNonce.toByteArray()
                val md = java.security.MessageDigest.getInstance("SHA-256")
                val digest = md.digest(bytes)
                val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

                val googleIdOption =
                        GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                                .setNonce(hashedNonce)
                                .setAutoSelectEnabled(false)
                                .build()

                val request =
                        GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

                val result = credentialManager.getCredential(context = context, request = request)
                val credential = result.credential
                if (credential is GoogleIdTokenCredential) {
                        val googleIdToken = credential.idToken
                        // Pass the raw nonce we generated, so Supabase can verify the ID token's
                        // nonce claim
                        viewModel.signInWithGoogle(googleIdToken, rawNonce)
                } else {
                        Log.e("LoginScreen", "Unexpected credential type")
                }
        } catch (e: androidx.credentials.exceptions.GetCredentialCancellationException) {
                Log.d("LoginScreen", "Sign in cancelled by user")
                // Do nothing or show a simple message
        } catch (e: GetCredentialException) {
                Log.e("LoginScreen", "GetCredentialException", e)
                android.widget.Toast.makeText(
                                context,
                                "Error: ${e.message}",
                                android.widget.Toast.LENGTH_LONG
                        )
                        .show()
        } catch (e: Exception) {
                Log.e("LoginScreen", "Exception", e)
                android.widget.Toast.makeText(
                                context,
                                "Error desconocido: ${e.message}",
                                android.widget.Toast.LENGTH_LONG
                        )
                        .show()
        }
}
