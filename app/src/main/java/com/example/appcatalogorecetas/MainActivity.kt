package com.example.appcatalogorecetas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appcatalogorecetas.data.local.PreferenciasDataStore
import com.example.appcatalogorecetas.ui.PantallaAjustes
import com.example.appcatalogorecetas.ui.PantallaCatalogo
import com.example.appcatalogorecetas.ui.PantallaDetalles
import com.example.appcatalogorecetas.ui.PantallaFavoritos
import com.example.appcatalogorecetas.ui.RecetaViewModel
import com.example.appcatalogorecetas.ui.theme.AppCatalogoRecetasTheme

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

            NavHost(navController = navController, startDestination = "catalogo") {

                composable("catalogo") {
                    PantallaCatalogo(navController = navController, viewModel = recetaViewModel)
                }

                composable("favoritos") {
                    PantallaFavoritos(navController = navController, viewModel = recetaViewModel)
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
            }
        }
    }
}