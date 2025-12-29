package com.devdiaz.gritia.ui.login

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
        viewModel: AuthViewModel = hiltViewModel()
) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val sessionStatus by viewModel.sessionStatus.collectAsState()

        // Handle authentication state changes
        LaunchedEffect(sessionStatus) {
                Log.d("LoginScreen", "Session status observed: $sessionStatus")
                if (sessionStatus is SessionStatus.Authenticated) {
                        Log.d("LoginScreen", "Authenticated! Redirecting to main...")
                        onLoginSuccess()
                } else {
                        Log.d("LoginScreen", "Not authenticated, staying on LoginScreen.")
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
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                                text = "Inicia sesión para continuar",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(24.dp))

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
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                        text = (sessionStatus as SessionStatus.Error).message,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                )
                        }
                }
        }
}

private suspend fun signInWithGoogle(context: android.content.Context, viewModel: AuthViewModel) {
        try {
                val credentialManager = CredentialManager.create(context)
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
                        viewModel.signInWithGoogle(googleIdToken, rawNonce)
                } else {
                        Log.e("LoginScreen", "Unexpected credential type")
                }
        } catch (e: androidx.credentials.exceptions.GetCredentialCancellationException) {
                Log.d("LoginScreen", "Sign in cancelled by user")
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
