package com.example.appcatalogorecetas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun PantallaPerfil(navController: NavController, viewModel: RecetaViewModel) {

    val favoritas by viewModel.recetasFavoritas.collectAsState()

    Scaffold(
        bottomBar = { BarraInferior(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Perfil",
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Mi perfil", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        "AppCatalogoRecetas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${favoritas.size}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Recetas favoritas", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(24.dp))

            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column {
                    ItemMenuPerfil(
                        icono = Icons.Filled.Favorite,
                        texto = "Mis favoritos",
                        onClick = { navController.navigate("favoritos") }
                    )
                    HorizontalDivider()
                    ItemMenuPerfil(
                        icono = Icons.Filled.Settings,
                        texto = "Configuración",
                        onClick = { navController.navigate("ajustes") }
                    )
                }
            }
        }
    }
}

@Composable
private fun ItemMenuPerfil(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    texto: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, contentDescription = null)
        Spacer(Modifier.width(16.dp))
        Text(texto, modifier = Modifier.weight(1f))
        Icon(Icons.Filled.ChevronRight, contentDescription = null)
    }
}

