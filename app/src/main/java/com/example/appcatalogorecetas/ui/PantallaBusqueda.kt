package com.example.appcatalogorecetas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun PantallaBusqueda(navController: NavController, viewModel: RecetaViewModel) {

    val recetas by viewModel.recetasApi.collectAsState()
    val favoritas by viewModel.recetasFavoritas.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()
    var textoBusqueda by remember { mutableStateOf("") }

    val idsFavoritos = favoritas.map { it.id }.toSet()

    Scaffold(
        topBar = {
            Text(
                text = "Búsqueda",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(20.dp)
            )
        },
        bottomBar = { BarraInferior(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = {
                    textoBusqueda = it
                    if (it.length > 2) viewModel.buscarRecetas(it)
                },
                label = { Text("Buscar receta (ej: chicken, pasta)...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                cargando -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = error ?: "Ocurrió un error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(recetas) { receta ->
                            ItemReceta(
                                receta = receta,
                                esFavorito = receta.id in idsFavoritos,
                                onClick = { navController.navigate("detalles/${receta.id}") },
                                onToggleFavorito = {
                                    if (receta.id in idsFavoritos) {
                                        viewModel.eliminarFavorito(receta)
                                    } else {
                                        viewModel.guardarFavorito(receta)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

