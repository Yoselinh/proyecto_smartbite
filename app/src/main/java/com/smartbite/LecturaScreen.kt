package com.smartbite

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.smartbite.model.LecturaSensor
import com.smartbite.viewmodel.LecturaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LecturaScreen(
    navController: NavController,
    lecturaViewModel: LecturaViewModel = viewModel()
) {
    // Observables
    val lecturasNullable by lecturaViewModel.lecturasLiveData.observeAsState()
    val lecturas = lecturasNullable ?: emptyList()
    val cargando by lecturaViewModel.cargandoLiveData.observeAsState(false)
    val error by lecturaViewModel.errorLiveData.observeAsState()

    // Cargando datos al entrar
    LaunchedEffect(Unit) {
        lecturaViewModel.obtenerLecturas()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Lecturas del Plato Inteligente") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver al inicio")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Filled.Home, contentDescription = "Ir al inicio")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (cargando) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                if (lecturas.isEmpty()) {
                    Text("No hay lecturas disponibles aún.", color = Color.Gray, modifier = Modifier.padding(8.dp))
                } else {
                    val ultima = lecturas.last()
                    Text("Última lectura:", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Tarjetas principales para última lectura
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TinySensorCard("Carbohidratos", "${ultima.peso_carbohidrato}", Color(0xFFFFB74D))
                        TinySensorCard("Proteínas", "${ultima.peso_proteina}", Color(0xFF64B5F6))
                        TinySensorCard("Vegetales", "${ultima.peso_vegetal}", Color(0xFF81C784))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Fecha: ${ultima.fecha_hora}", color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lista de lecturas previas (invertida para ver las más recientes arriba)
                    Text("Historial de lecturas", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(modifier = Modifier.fillMaxHeight(0.6f)) {
                        items(items = lecturas.asReversed(), key = { it.id }) { item ->
                            LecturaListItem(item) {
                                // onClick: por ejemplo, mostrar detalle o copiar valor
                                // ahora sólo hacemos un toast o nada
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botones
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = { lecturaViewModel.obtenerLecturas() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Actualizar")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Actualizar")
                    }
                    OutlinedButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Filled.Home, contentDescription = "Inicio")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Inicio")
                    }
                }

                if (!error.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(error ?: "", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun TinySensorCard(title: String, value: String, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, color = color)
        }
    }
}

@Composable
fun LecturaListItem(item: LecturaSensor, onClick: () -> Unit) {
    Card(shape = RoundedCornerShape(12.dp), modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("ID: ${item.id}", style = MaterialTheme.typography.bodySmall)
                Text("C: ${item.peso_carbohidrato}  P: ${item.peso_proteina}  V: ${item.peso_vegetal}", style = MaterialTheme.typography.bodyMedium)
            }
            Text(item.fecha_hora, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
