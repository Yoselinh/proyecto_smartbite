package com.smartbite

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.runtime.remember
import com.smartbite.repository.LecturaRepository
import com.smartbite.viewmodel.LecturaViewModel
import com.smartbite.api.ApiClient
import com.smartbite.viewmodel.LecturaViewModelFactory

@Composable
fun NavGraph(navController: NavHostController) {

    val lecturaRepository = remember {
        LecturaRepository(ApiClient.apiService)
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(navController)
        }

        composable("register") {
            RegisterScreen(navController)
        }

        composable("home") {

            val lecturaViewModel: LecturaViewModel = viewModel(
                modelClass = LecturaViewModel::class.java,
                factory = LecturaViewModelFactory(lecturaRepository)
            )

            HomeScreen(navController, lecturaViewModel)
        }

        composable("lecturas") {

            val lecturaViewModel: LecturaViewModel = viewModel(
                modelClass = LecturaViewModel::class.java,
                factory = LecturaViewModelFactory(lecturaRepository)
            )

            LecturaScreen(
                navController = navController,
                viewModel = lecturaViewModel
            )
        }

        composable("perfil") {
            PerfilScreen(navController)
        }
    }
}
