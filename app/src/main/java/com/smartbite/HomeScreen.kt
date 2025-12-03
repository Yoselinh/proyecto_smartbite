package com.smartbite

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.smartbite.viewmodel.LecturaViewModel
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import android.content.Context

// Paleta solicitada
private val Peach = Color(0xFFFFD8B5)
private val OrangePastel = Color(0xFFFFB980)
private val BeigeLight = Color(0xFFFFF5E8)
private val SoftGray = Color(0xFF6D6D6D)
private val SoftPurple = Color(0xFFD7C4FF)
private val SoftRed = Color(0xFFFFA49A)
private val SoftGreen = Color(0xFF9ED8A6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, lecturaViewModel: LecturaViewModel) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("smartbite_prefs", Context.MODE_PRIVATE)

    var metaProte by remember { mutableStateOf(prefs.getInt("meta_prote", 1000)) }
    var metaCarbo by remember { mutableStateOf(prefs.getInt("meta_carbo", 1800)) }
    var metaVegetal by remember { mutableStateOf(prefs.getInt("meta_vegetal", 500)) }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { entry ->
            if (entry.destination.route == "home") {
                metaProte = prefs.getInt("meta_prote", 1000)
                metaCarbo = prefs.getInt("meta_carbo", 1800)
                metaVegetal = prefs.getInt("meta_vegetal", 500)
            }
        }
    }

    // Observa el StateFlow de lecturas
    val lecturas by lecturaViewModel.misLecturas.collectAsState()
    LaunchedEffect(Unit) {
        lecturaViewModel.cargarToken(context)
        lecturaViewModel.cargarMisLecturas()
    }


    // Obtiene resumen del día desde el ViewModel
    val resumenHoy by remember {
        derivedStateOf { lecturaViewModel.obtenerResumenDeHoy() }
    }
    val (proteinaHoy, carboHoy, vegetalHoy) = resumenHoy

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(OrangePastel),
                drawerContainerColor = OrangePastel
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.avatar),
                        contentDescription = "Perfil",
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "SmartBite",
                        color = SoftGray,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Divider(color = Color.White.copy(alpha = 0.3f))

                DrawerItem("Perfil") {
                    navController.navigate("perfil")
                    scope.launch { drawerState.close() }
                }

                DrawerItem("Mis lecturas registradas") {
                    navController.navigate("lecturas")
                    scope.launch { drawerState.close() }
                }

                DrawerItem("Cerrar sesión") {
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            }
        }
    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("SmartBite", color = SoftGray) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
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
                    .padding(16.dp)
            ) {

                // -------------------- TITULO --------------------
                Text(
                    "Bienvenido 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    color = SoftGray
                )
                Text(
                    "Aquí tienes tu resumen nutricional de hoy",
                    color = SoftGray.copy(alpha = 0.7f)
                )

                Spacer(Modifier.height(20.dp))

                // -------------------- TARJETAS DEL DÍA --------------------
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    NutrientCard(
                        titulo = "Proteínas",
                        valor = proteinaHoy,
                        color = SoftGreen
                    )

                    NutrientCard(
                        titulo = "Carbohidratos",
                        valor = carboHoy,
                        color = SoftPurple
                    )

                    NutrientCard(
                        titulo = "Vegetales",
                        valor = vegetalHoy,
                        color = OrangePastel
                    )
                }

                Spacer(Modifier.height(20.dp))

                // -------------------- OBJETIVOS  --------------------
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SoftGreen.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {

                        Text(
                            "Objetivos ",
                            style = MaterialTheme.typography.titleMedium,
                            color = SoftGray
                        )

                        Spacer(Modifier.height(12.dp))

                        WeeklyGoalRow("Proteínas", proteinaHoy.toInt(), metaProte)
                        WeeklyGoalRow("Carbohidratos", carboHoy.toInt(), metaCarbo)
                        WeeklyGoalRow("Vegetales", vegetalHoy.toInt(), metaVegetal)
                    }
                }


                Spacer(Modifier.height(20.dp))

                // -------------------- ÚLTIMAS LECTURAS --------------------
                Text(
                    "Tus últimas lecturas",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoftGray
                )

                Spacer(Modifier.height(12.dp))

                lecturas.takeLast(6).reversed().forEach { lectura ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftPurple.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(14.dp)
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

                Spacer(Modifier.height(20.dp))

                // -------------------- TIPS NUTRICIONALES --------------------
                Text(
                    "Tips nutricionales",
                    style = MaterialTheme.typography.titleMedium,
                    color = SoftGray
                )

                Spacer(Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = OrangePastel.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {

                        Text(
                            "🥦 Incluye siempre vegetales verdes en tu plato.",
                            color = SoftGray
                        )
                        Spacer(Modifier.height(8.dp))

                        Text(
                            "🍗 Prefiere proteínas magras como pollo o pescado.",
                            color = SoftGray
                        )
                        Spacer(Modifier.height(8.dp))

                        Text(
                            "💧 Toma al menos 6 vasos de agua al día.",
                            color = SoftGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NutrientCard(titulo: String, valor: Double, color: Color) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .height(130.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(titulo, color = SoftGray)
            Spacer(Modifier.height(10.dp))
            Text(
                "${valor.toInt()} g",
                style = MaterialTheme.typography.headlineSmall,
                color = SoftGray
            )
        }
    }
}

@Composable
fun DrawerItem(texto: String, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(texto, color = Color.White) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
    )
}

@Composable
fun WeeklyGoalRow(nombre: String, valorActual: Int, meta: Int) {
    val progreso = (valorActual.toFloat() / meta).coerceIn(0f, 1f)

    Column(Modifier.fillMaxWidth()) {
        Text("$nombre: $valorActual / $meta g", color = SoftGray)
        Spacer(Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = progreso,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50)),
            color = SoftGray,
            trackColor = Color.White.copy(alpha = 0.4f)
        )

        Spacer(Modifier.height(12.dp))
    }
}
