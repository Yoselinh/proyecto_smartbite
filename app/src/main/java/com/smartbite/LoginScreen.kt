package com.smartbite

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.runtime.livedata.observeAsState
import com.smartbite.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel = viewModel()
) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val usuarioState by usuarioViewModel.usuarioLiveData.observeAsState()
    val errorState by usuarioViewModel.errorLiveData.observeAsState()
    val cargando by usuarioViewModel.cargandoLiveData.observeAsState(false)

    val context = LocalContext.current  // ← Contexto necesario para guardar el token

    // 🔹 Navegación automática y guardado del token si el login fue exitoso
    LaunchedEffect(usuarioState) {
        usuarioState?.let { user ->
            val tokenRecibido = user.token  // ← Obtiene el token de la respuesta

            if (!tokenRecibido.isNullOrEmpty()) {
                // Guarda el token en SharedPreferences
                val prefs = context.getSharedPreferences("smartbite_prefs", Context.MODE_PRIVATE)
                prefs.edit().putString("auth_token", tokenRecibido).apply()
                println("✅ Token guardado correctamente: $tokenRecibido")
            }

            // Navega a la pantalla principal
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Iniciar Sesión", style = MaterialTheme.typography.titleLarge) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { usuarioViewModel.login(correo, password) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !cargando
            ) {
                Text(if (cargando) "Cargando..." else "Ingresar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate("register") }) {
                Text("¿No tienes cuenta? Regístrate aquí")
            }

            // 🔹 Mostrar error si existe
            if (!errorState.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorState ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
