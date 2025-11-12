package com.smartbite

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController) {
    // Valores de ejemplo para las barras (más tarde vendrán del ViewModel)
    val pesoValue = remember { 180f }     // ejemplo: gramos
    val caloriasValue = remember { 320f } // ejemplo: kcal
    val tempValue = remember { 36.5f }    // ejemplo: °C

    // Rangos para normalizar las barras (ajusta según tus datos reales)
    val maxPeso = 300f
    val maxCalorias = 1000f
    val maxTemp = 42f

    val pesoFraction = (pesoValue / maxPeso).coerceIn(0f, 1f)
    val caloriasFraction = (caloriasValue / maxCalorias).coerceIn(0f, 1f)
    val tempFraction = (tempValue / maxTemp).coerceIn(0f, 1f)

    // Año actual compatible con Android
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "SmartBite",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF6A5ACD),
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { navController.navigate("login") }) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF6F4FB))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "¡Bienvenido a SmartBite! 👋",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A148C)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Monitorea tus sensores y controla tus porciones", color = Color.Gray)
            }

            // Tarjeta con "gráfico" sencillo hecho con Boxes (sin librerías externas)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Lecturas de sensores", fontWeight = FontWeight.Bold, color = Color(0xFF4A148C))

                    SensorBarRow(label = "Peso (g)", fraction = pesoFraction, display = "${pesoValue.toInt()} g", barColor = Color(0xFF81C784))
                    SensorBarRow(label = "Calorías", fraction = caloriasFraction, display = "${caloriasValue.toInt()} kcal", barColor = Color(0xFFFFB74D))
                }
            }

            // Botones principales
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { navController.navigate("perfil") },
                    modifier = Modifier.fillMaxWidth(0.85f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A5ACD))
                ) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ver / Editar Perfil", color = Color.White)
                }

                OutlinedButton(
                    onClick = { navController.navigate("lectura") },
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    Text("Ver Lecturas de Sensores")
                }

                OutlinedButton(
                    onClick = { navController.navigate("login") },
                    modifier = Modifier.fillMaxWidth(0.85f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Text("Cerrar sesión", color = Color.Red)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("SmartBite © $currentYear", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun SensorBarRow(label: String, fraction: Float, display: String, barColor: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label)
            Text(display, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        // Fondo de la barra
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(22.dp)
                .background(Color(0xFFECEFF1), shape = RoundedCornerShape(12.dp))
        ) {
            // Barra llena proporcional
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction)
                    .background(barColor, shape = RoundedCornerShape(12.dp))
            )
        }
    }
}
