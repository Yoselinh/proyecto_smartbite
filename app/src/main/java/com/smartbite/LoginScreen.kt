package com.smartbite

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smartbite.api.ApiClient
import com.smartbite.viewmodel.UsuarioViewModel
import com.smartbite.viewmodel.LecturaViewModel
import com.smartbite.viewmodel.LecturaViewModelFactory
import com.smartbite.repository.LecturaRepository
import android.util.Log


// 🎨 Paleta Pastel
private val Peach = Color(0xFFFFD8B5)
private val OrangePastel = Color(0xFFFFB980)
private val BeigeLight = Color(0xFFFFF5E8)
private val SoftGray = Color(0xFF6D6D6D)

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

    val context = LocalContext.current

    val lecturaViewModel: LecturaViewModel = viewModel(
        factory = LecturaViewModelFactory(
            LecturaRepository(ApiClient.apiService)
        )
    )

    // Navegación automática si el login es exitoso
    LaunchedEffect(usuarioState) {
        usuarioState?.let { user ->

            // 1. Guardar token
            val prefs = context.getSharedPreferences("smartbite_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("auth_token", user.token)
                .putLong("user_id", user.userId.toLong())   // <-- necesario
                .apply()

            // 2. (OPCIONAL) Obtener perfil si quieres mostrar los datos
            //    Tu perfil NO devuelve id, así que no se puede guardar.
            try {
                val perfilResponse = ApiClient.apiService.obtenerPerfil("Bearer ${user.token}")

                if (!perfilResponse.isSuccessful) {
                    Log.e("LoginScreen", "Error obteniendo perfil")
                }

            } catch (e: Exception) {
                Log.e("LoginScreen", "Excepción obteniendo perfil: ${e.message}")
            }

            // 4. Navegar a Home
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Scaffold(
        containerColor = BeigeLight
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo3),
                contentDescription = "SmartBite Logo",
                modifier = Modifier.size(160.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Bienvenido a SmartBite",
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                fontWeight = FontWeight.Bold,
                color = SoftGray
            )

            Spacer(modifier = Modifier.height(30.dp))

            // TARJETA ESTÉTICA
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Peach)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
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

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = { usuarioViewModel.login(correo, password, context) }, // <- pasar context aquí
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePastel),
                        enabled = !cargando
                    ) {
                        Text(
                            if (cargando) "Cargando..." else "Ingresar",
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            TextButton(onClick = { navController.navigate("register") }) {
                Text("¿No tienes cuenta? Regístrate aquí", color = SoftGray)
            }

            if (!errorState.isNullOrEmpty()) {
                Text(
                    text = errorState ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}
