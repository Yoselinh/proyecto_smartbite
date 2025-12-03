package com.smartbite

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.smartbite.model.PerfilUsuario
import com.smartbite.viewmodel.PerfilViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import android.widget.Toast

// 🎨 Paleta Pastel
private val Peach = Color(0xFFFFD8B5)
private val OrangePastel = Color(0xFFFFB980)
private val BeigeLight = Color(0xFFFFF5E8)
private val SoftGray = Color(0xFF6D6D6D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    viewModel: PerfilViewModel = viewModel()
) {

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("smartbite_prefs", Context.MODE_PRIVATE)
    val editor = prefs.edit()

    val token = prefs.getString("auth_token", null)
    val userId = prefs.getLong("user_id", 0L) // ID del usuario actual


    val perfil by viewModel.perfilLiveData.observeAsState()
    val cargando by viewModel.cargandoLiveData.observeAsState(false)
    val error by viewModel.errorLiveData.observeAsState()

    var peso by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var modoEdicion by remember { mutableStateOf(false) }

    // -----------------------------
    // OBJETIVO DEL USUARIO
    // -----------------------------
    val objetivoGuardado = prefs.getString("objetivo_$userId", "mantener") ?: "mantener"
    val objetivo by viewModel.objetivoLiveData.observeAsState(objetivoGuardado)

    // -----------------------------
    // METAS GUARDADAS POR USUARIO
    // -----------------------------
    val metaProteGuardada = prefs.getInt("meta_prote_$userId", 1000)
    val metaCarboGuardada = prefs.getInt("meta_carbo_$userId", 1800)
    val metaVegetalGuardada = prefs.getInt("meta_vegetal_$userId", 500)

    var metaPState by remember { mutableStateOf(metaProteGuardada) }
    var metaCState by remember { mutableStateOf(metaCarboGuardada) }
    var metaVState by remember { mutableStateOf(metaVegetalGuardada) }

    // Cargar perfil al abrir pantalla
    LaunchedEffect(token) {
        if (!token.isNullOrEmpty()) viewModel.obtenerPerfil(token)
    }

    // Sync con datos del perfil
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
                title = { Text("Mi Perfil", color = SoftGray) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Peach
                )
            )
        },
        containerColor = BeigeLight
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            // Imagen
            Image(
                painter = painterResource(id = R.drawable.avatar),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(24.dp))


            // -----------------------------
            // CARD DEL FORMULARIO
            // -----------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Peach)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

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

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botones guardar / editar
                    if (modoEdicion) {
                        Button(
                            onClick = {
                                if (!token.isNullOrEmpty()) {
                                    viewModel.actualizarPerfil(
                                        token,
                                        PerfilUsuario(
                                            peso = peso.toFloatOrNull(),
                                            altura = altura.toFloatOrNull(),
                                            edad = edad.toIntOrNull(),
                                            genero = genero
                                        )
                                    )
                                }
                                modoEdicion = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePastel)
                        ) {
                            Text("Guardar cambios")
                        }
                    } else {
                        Button(
                            onClick = { modoEdicion = true },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePastel)
                        ) {
                            Text("Editar perfil")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))


            // ---------------------------------------------------------
            //  CARD DE OBJETIVO Y CÁLCULO DE METAS
            // ---------------------------------------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {

                    Text(
                        text = "Selecciona tu objetivo",
                        style = MaterialTheme.typography.titleMedium,
                        color = SoftGray
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = objetivo == "subir",
                                onClick = {
                                    viewModel.objetivoLiveData.value = "subir"
                                    editor.putString("objetivo_$userId", "subir").apply()
                                }
                            )
                            Text("Subir peso")
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = objetivo == "bajar",
                                onClick = {
                                    viewModel.objetivoLiveData.value = "bajar"
                                    editor.putString("objetivo_$userId", "bajar").apply()
                                }
                            )
                            Text("Bajar peso")
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = objetivo == "mantener",
                                onClick = {
                                    viewModel.objetivoLiveData.value = "mantener"
                                    editor.putString("objetivo_$userId", "mantener").apply()
                                }
                            )
                            Text("Mantener peso")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val pesoFloat = peso.toFloatOrNull()
                            if (pesoFloat == null) {
                                Toast.makeText(context, "Peso inválido", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // Calcular metas según el objetivo actual
                            viewModel.calcularMetasYAplicarlas(pesoFloat, userId, context)

                            // Actualizar los estados locales con los LiveData recién calculados
                            metaPState = viewModel.metaProteinaLiveData.value ?: 0
                            metaCState = viewModel.metaCarboLiveData.value ?: 0
                            metaVState = viewModel.metaVegetalLiveData.value ?: 0

                            // Guardar metas en SharedPreferences
                            editor.putInt("meta_prote_$userId", metaPState)
                            editor.putInt("meta_carbo_$userId", metaCState)
                            editor.putInt("meta_vegetal_$userId", metaVState)
                            editor.putString("objetivo_$userId", viewModel.objetivoLiveData.value ?: "mantener")
                            editor.apply()

                            Toast.makeText(context, "Metas calculadas", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePastel)
                    ) {
                        Text("Calcular metas")
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
