package com.smartbite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.smartbite.viewmodel.LecturaViewModel
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.sp

// 🎨 Paleta Pastel
private val Peach = Color(0xFFFFD8B5)
private val OrangePastel = Color(0xFFFFB980)
private val BeigeLight = Color(0xFFFFF5E8)
private val SoftGray = Color(0xFF6D6D6D)
private val SoftWhite = Color(0xFFFFFFFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LecturaScreen(
    navController: NavHostController,
    viewModel: LecturaViewModel
) {
    val context = LocalContext.current

    // Usamos el ViewModel enviado desde el NavGraph
    val lecturaViewModel = viewModel

    LaunchedEffect(Unit) {
        lecturaViewModel.cargarToken(context)
        lecturaViewModel.iniciarMQTT()

        while (true) {
            delay(2000)
            lecturaViewModel.cargarMisLecturas()
        }
    }

    // Estados del ViewModel
    val mensaje by lecturaViewModel.mensaje.collectAsState()
    val lecturas by lecturaViewModel.misLecturas.collectAsState()

    val pesoP by lecturaViewModel.ultimoPesoProteina.collectAsState()
    val pesoC by lecturaViewModel.ultimoPesoCarbohidrato.collectAsState()
    val pesoV by lecturaViewModel.ultimoPesoVegetal.collectAsState()

    //  Estados para nombres de alimentos
    var nombreProteina by remember { mutableStateOf("") }
    var nombreCarbohidrato by remember { mutableStateOf("") }
    var nombreVegetal by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Lectura del Plato", color = SoftGray) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Peach)
            )
        },
        containerColor = BeigeLight
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {

            // 🟢 Pesos en tiempo real
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SoftWhite),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text("Pesos del plato en tiempo real",
                        style = MaterialTheme.typography.titleMedium,
                        color = SoftGray
                    )
                    Spacer(Modifier.height(12.dp))

                    Text("🍗 Proteína: $pesoP g", color = SoftGray)
                    Text("🍞 Carbohidrato: $pesoC g", color = SoftGray)
                    Text("🥬 Vegetal: $pesoV g", color = SoftGray)
                }
            }

            Spacer(Modifier.height(25.dp))

            // 🟠 FORMULARIO PARA ELEGIR NOMBRES
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SoftWhite),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        "Registrar alimentos",
                        style = MaterialTheme.typography.titleMedium,
                        color = SoftGray
                    )

                    // 🔹 Nombre proteína
                    OutlinedTextField(
                        value = lecturaViewModel.nombreProteina.collectAsState().value,
                        onValueChange = {
                            lecturaViewModel.actualizarNombreProteina(it)
                        },
                        label = { Text("Nombre de la proteína") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 🔹 Nombre carbohidrato
                    OutlinedTextField(
                        value = lecturaViewModel.nombreCarbohidrato.collectAsState().value,
                        onValueChange = {
                            lecturaViewModel.actualizarNombreCarbohidrato(it)
                        },
                        label = { Text("Nombre del carbohidrato") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 🔹 Nombre vegetal
                    OutlinedTextField(
                        value = lecturaViewModel.nombreVegetal.collectAsState().value,
                        onValueChange = {
                            lecturaViewModel.actualizarNombreVegetal(it)
                        },
                        label = { Text("Nombre del vegetal") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    // BOTÓN GUARDAR LECTURA
                    Button(
                        onClick = {
                            lecturaViewModel.registrarNuevaLectura()

                            // Limpiar campos del VM
                            lecturaViewModel.actualizarNombreProteina("")
                            lecturaViewModel.actualizarNombreCarbohidrato("")
                            lecturaViewModel.actualizarNombreVegetal("")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePastel),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Guardar lectura")
                    }
                }
            }


            Spacer(Modifier.height(20.dp))

            // 📜 HISTORIAL DE LECTURAS
            Text("Historial de lecturas", color = SoftGray)

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(lecturas) { lectura ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftWhite),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🍗 Proteína: ${lectura.nombreProteina} (${lectura.pesoProteina} g)")
                            Text("🍞 Carbohidrato: ${lectura.nombreCarbohidrato} (${lectura.pesoCarbohidrato} g)")
                            Text("🥬 Vegetal: ${lectura.nombreVegetal} (${lectura.pesoVegetal} g)")

                            Spacer(Modifier.height(6.dp))
                            Divider()
                            Text("📅 ${lectura.fechaHora}", color = SoftGray)
                        }
                    }
                }
            }

            if (mensaje.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(Color(0xFF4CAF50), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = mensaje,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
