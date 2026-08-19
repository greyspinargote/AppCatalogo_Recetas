package com.example.appcatalogorecetas.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue

// Rutas que tienen su propio ícono en la barra inferior
enum class PantallaBarraInferior(val ruta: String, val etiqueta: String) {
    INICIO("inicio", "Inicio"),
    BUSCAR("buscar", "Buscar"),
    FAVORITOS("favoritos", "Favoritos"),
    PERFIL("perfil", "Perfil")
}

@Composable
fun BarraInferior(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    NavigationBar {
        PantallaBarraInferior.values().forEach { pantalla ->
            NavigationBarItem(
                selected = rutaActual == pantalla.ruta,
                onClick = {
                    if (rutaActual != pantalla.ruta) {
                        navController.navigate(pantalla.ruta) {
                            // Evita apilar copias de la misma pantalla al tocarla varias veces
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    val icono = when (pantalla) {
                        PantallaBarraInferior.INICIO -> Icons.Filled.Home
                        PantallaBarraInferior.BUSCAR -> Icons.Filled.Search
                        PantallaBarraInferior.FAVORITOS -> Icons.Filled.Favorite
                        PantallaBarraInferior.PERFIL -> Icons.Filled.Person
                    }
                    Icon(icono, contentDescription = pantalla.etiqueta)
                },
                label = { Text(pantalla.etiqueta) }
            )
        }
    }
}