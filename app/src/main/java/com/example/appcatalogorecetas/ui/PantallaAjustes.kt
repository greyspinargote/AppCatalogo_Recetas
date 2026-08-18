package com.example.appcatalogorecetas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appcatalogorecetas.data.local.PreferenciasDataStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAjustes(navController: NavController, preferencias: PreferenciasDataStore) {

    val modoOscuro by preferencias.modoOscuro.collectAsState(initial = false)
    val unidadMedida by preferencias.unidadMedida.collectAsState(initial = "Métrica")
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Configuración", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Preferencias",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                // MODO OSCURO
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Modo oscuro", modifier = Modifier.weight(1f))
                    Switch(
                        checked = modoOscuro,
                        onCheckedChange = { activado ->
                            scope.launch { preferencias.guardarModoOscuro(activado) }
                        }
                    )
                }

                HorizontalDivider()

                // UNIDAD DE MEDIDA
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Unidad de medida")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = unidadMedida == "Métrica",
                            onClick = {
                                scope.launch { preferencias.guardarUnidadMedida("Métrica") }
                            },
                            label = { Text("Métrica (g, ml)") }
                        )
                        FilterChip(
                            selected = unidadMedida == "Imperial",
                            onClick = {
                                scope.launch { preferencias.guardarUnidadMedida("Imperial") }
                            },
                            label = { Text("Imperial (oz, cups)") }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Acerca de la app",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "AppCatalogoRecetas · Versión 1.0.0",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}