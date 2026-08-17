package com.example.appcatalogorecetas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.appcatalogorecetas.data.local.Receta

@Composable
fun PantallaDetalles(navController: NavController, viewModel: RecetaViewModel, recetaId: String) {

    var recetaState by remember { mutableStateOf<Receta?>(null) }
    var cargando by remember { mutableStateOf(true) }

    val favoritas by viewModel.recetasFavoritas.collectAsState()
    val esFavorito = favoritas.any { it.id == recetaId }

    LaunchedEffect(recetaId) {
        cargando = true
        recetaState = viewModel.obtenerDetalle(recetaId)
        cargando = false
    }

    val receta = recetaState

    if (cargando) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (receta == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No se pudo cargar la receta.")
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                AsyncImage(
                    model = receta.imagenUrl,
                    contentDescription = receta.nombre,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.background
                                ),
                                startY = 250f
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .offset(y = (-32).dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = receta.nombre,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = {
                        if (esFavorito) viewModel.eliminarFavorito(receta)
                        else viewModel.guardarFavorito(receta)
                    }) {
                        Text(if (esFavorito) "❤️" else "🤍", fontSize = 24.sp)
                    }
                }

                Text(
                    text = receta.categoria ?: "General",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "Instrucciones", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = receta.instrucciones ?: "Sin instrucciones disponibles.",
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Volver", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
