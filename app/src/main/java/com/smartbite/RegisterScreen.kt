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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.smartbite.viewmodel.UsuarioViewModel

// 🎨 Paleta Pastel
private val Peach = Color(0xFFFFD8B5)
private val OrangePastel = Color(0xFFFFB980)
private val BeigeLight = Color(0xFFFFF5E8)
private val SoftGray = Color(0xFF6D6D6D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    usuarioViewModel: UsuarioViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val usuarioState by usuarioViewModel.usuarioLiveData.observeAsState()
    val errorState by usuarioViewModel.errorLiveData.observeAsState()
    val cargando by usuarioViewModel.cargandoLiveData.observeAsState(false)

    val context = LocalContext.current

    // Si el registro fue exitoso → ir al Home
    LaunchedEffect(usuarioState) {
        usuarioState?.let {
            navController.navigate("home") {
                popUpTo("register") { inclusive = true }
            }
        }
    }


    Scaffold(
        containerColor = BeigeLight
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo3),
                contentDescription = "SmartBite Logo",
                modifier = Modifier.size(140.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Crear una cuenta",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = SoftGray
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Tarjeta estética pastel
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
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre completo") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

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

                    Spacer(modifier = Modifier.height(22.dp))

                    Button(
                        onClick = {
                            usuarioViewModel.registrar(
                                nombre = nombre,
                                correo = correo,
                                password = password,
                                context = context,
                                rol = "USER"
                            )

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePastel),
                        enabled = !cargando
                    ) {
                        Text(
                            if (cargando) "Registrando..." else "Registrar",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextButton(onClick = { navController.navigate("login") }) {
                Text("¿Ya tienes cuenta? Inicia sesión", color = SoftGray)
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
