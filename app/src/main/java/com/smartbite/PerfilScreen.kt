package com.smartbite

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.smartbite.model.PerfilUsuario
import com.smartbite.viewmodel.PerfilViewModel
import androidx.compose.runtime.livedata.observeAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    viewModel: PerfilViewModel = viewModel()
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("smartbite_prefs", Context.MODE_PRIVATE)
    val token = prefs.getString("auth_token", null)

    val perfil by viewModel.perfilLiveData.observeAsState()
    val cargando by viewModel.cargandoLiveData.observeAsState(false)
    val error by viewModel.errorLiveData.observeAsState()

    var peso by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var modoEdicion by remember { mutableStateOf(false) }

    // 🔹 Cargar perfil cada vez que entramos a esta pantalla
    LaunchedEffect(token) {
        if (!token.isNullOrEmpty()) {
            viewModel.obtenerPerfil(token)
        }
    }

    // Actualizar los campos cuando cambia el perfil
    LaunchedEffect(perfil) {
        perfil?.let {
            peso = it.peso?.toString() ?: ""
            altura = it.altura?.toString() ?: ""
            edad = it.edad?.toString() ?: ""
            genero = it.genero ?: ""
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi Perfil") },
                actions = {
                    IconButton(onClick = {
                        // 🔹 Regresar al home sin cerrar app
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.Home, contentDescription = "Inicio")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (cargando && perfil == null) {
                // 🔹 Mostrar cargando solo la primera vez
                CircularProgressIndicator()
            } else {
                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it },
                    label = { Text("Peso (kg)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = modoEdicion
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = altura,
                    onValueChange = { altura = it },
                    label = { Text("Altura (m)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = modoEdicion
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = edad,
                    onValueChange = { edad = it },
                    label = { Text("Edad") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = modoEdicion
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = genero,
                    onValueChange = { genero = it },
                    label = { Text("Género") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = modoEdicion
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (modoEdicion) {
                    Button(
                        onClick = {
                            if (token.isNullOrEmpty()) {
                                viewModel.errorLiveData.postValue("Token no encontrado. Inicie sesión nuevamente.")
                            } else {
                                val perfilActualizado = PerfilUsuario(
                                    peso = peso.toFloatOrNull(),
                                    altura = altura.toFloatOrNull(),
                                    edad = edad.toIntOrNull(),
                                    genero = genero
                                )
                                viewModel.actualizarPerfil(token, perfilActualizado)

                                // 🔹 Luego de guardar, refrescar el perfil desde la API
                                viewModel.obtenerPerfil(token)
                                modoEdicion = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar Cambios")
                    }
                } else {
                    Button(
                        onClick = { modoEdicion = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Editar Perfil")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!error.isNullOrEmpty()) {
                    Text(
                        text = error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
