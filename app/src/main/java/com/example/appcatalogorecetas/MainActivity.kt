package com.example.appcatalogorecetas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appcatalogorecetas.data.local.PreferenciasDataStore
import com.example.appcatalogorecetas.ui.PantallaAjustes
import com.example.appcatalogorecetas.ui.PantallaCatalogo
import com.example.appcatalogorecetas.ui.PantallaDetalles
import com.example.appcatalogorecetas.ui.PantallaFavoritos
import com.example.appcatalogorecetas.ui.PantallaInicio
import com.example.appcatalogorecetas.ui.RecetaViewModel
import com.example.appcatalogorecetas.ui.theme.AppCatalogoRecetasTheme
import com.example.appcatalogorecetas.ui.PantallaBusqueda
import com.example.appcatalogorecetas.ui.PantallaCategorias

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecetarioApp()
        }
    }
}

@Composable
fun RecetarioApp() {
    val context = LocalContext.current
    val preferencias = remember { PreferenciasDataStore(context) }
    val modoOscuro by preferencias.modoOscuro.collectAsState(initial = false)

    AppCatalogoRecetasTheme(darkTheme = modoOscuro) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            val recetaViewModel: RecetaViewModel = viewModel()

            NavHost(navController = navController, startDestination = "bienvenida") {

                composable("bienvenida") {
                    PantallaBienvenida(navController = navController)
                }

                composable("inicio") {
                    PantallaInicio(navController = navController, viewModel = recetaViewModel)
                }

                composable("buscar") {
                    PantallaBusqueda(navController = navController, viewModel = recetaViewModel)
                }

                composable("favoritos") {
                    PantallaFavoritos(navController = navController, viewModel = recetaViewModel)
                }
                composable("categorias") {
                    PantallaCategorias(navController = navController, viewModel = recetaViewModel)
                }

                composable("detalles/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id") ?: ""
                    PantallaDetalles(
                        navController = navController,
                        viewModel = recetaViewModel,
                        recetaId = id
                    )
                }

                composable("ajustes") {
                    PantallaAjustes(navController = navController, preferencias = preferencias)
                }

                composable("perfil") {
                    PantallaAjustes(navController = navController, preferencias = preferencias)
                }
            }
        }
    }
}

@Composable
fun PantallaBienvenida(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AppCatalogoRecetas",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Descubre, cocina y guarda tus recetas favoritas.",
            modifier = Modifier.padding(
                top = 16.dp,
                bottom = 24.dp
            )
        )

        Button(
            onClick = {
                navController.navigate("inicio") {
                    popUpTo("bienvenida") { inclusive = true }
                }
            }
        ) {
            Text("Comenzar")
        }
    }
}