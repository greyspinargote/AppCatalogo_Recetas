package com.example.appcatalogorecetas.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import java.util.Locale

// La lista de categorías (categoriasInicio) vive en CategoriasData.kt

@Composable
fun PantallaInicio(navController: NavController, viewModel: RecetaViewModel) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val recetasApi by viewModel.recetasApi.collectAsState()
    val novedad = recetasApi.firstOrNull()

    // Estado de la ubicación: null = todavía no se activó
    var textoUbicacion by remember { mutableStateOf<String?>(null) }
    var ubicacionNoDisponible by remember { mutableStateOf(false) }
    var cargandoUbicacion by remember { mutableStateOf(false) }

    val clienteUbicacion = remember { LocationServices.getFusedLocationProviderClient(context) }

    fun obtenerUbicacion() {
        cargandoUbicacion = true
        ubicacionNoDisponible = false
        scope.launch {
            try {
                val ubicacion = clienteUbicacion.lastLocation.await()
                if (ubicacion != null) {
                    val geocoder = Geocoder(context, Locale("es", "EC"))
                    @Suppress("DEPRECATION")
                    val direcciones = geocoder.getFromLocation(ubicacion.latitude, ubicacion.longitude, 1)
                    val lugar = direcciones?.firstOrNull()
                    textoUbicacion = if (lugar != null) {
                        "${lugar.locality ?: lugar.subAdminArea ?: ""}, ${lugar.countryName ?: ""}"
                    } else {
                        "Lat: %.4f, Long: %.4f".format(ubicacion.latitude, ubicacion.longitude)
                    }
                } else {
                    ubicacionNoDisponible = true
                }
            } catch (e: Exception) {
                ubicacionNoDisponible = true
            } finally {
                cargandoUbicacion = false
            }
        }
    }

    // Pide el permiso de ubicación en tiempo de ejecución (mismo patrón que la cámara en Detalles)
    val lanzadorPermisoUbicacion = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            obtenerUbicacion()
        } else {
            ubicacionNoDisponible = true
        }
    }

    fun activarUbicacion() {
        val permisoActual = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permisoActual == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacion()
        } else {
            lanzadorPermisoUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Scaffold(
        bottomBar = { BarraInferior(navController) }
    ) { paddingInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // 👋 Saludo
            Text(
                text = "¡Hola! 👋",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            // 📍 Ubicación / GPS
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Recetas cerca de ti", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(8.dp))

                    when {
                        cargandoUbicacion -> Text("Buscando tu ubicación...")
                        textoUbicacion != null -> Text(textoUbicacion!!)
                        ubicacionNoDisponible -> Text(
                            "Ubicación no disponible. Puedes seguir usando la app sin activarla."
                        )
                        else -> Text("Activa tu ubicación para descubrir recetas cerca de ti.")
                    }

                    Spacer(Modifier.height(12.dp))

                    Button(onClick = { activarUbicacion() }) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text(if (textoUbicacion != null) "Actualizar ubicación" else "Activar ubicación")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // 🍝 Novedades de la semana
            Text("Novedades de la semana", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            if (novedad != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    onClick = { navController.navigate("detalles/${novedad.id}") }
                ) {
                    Column {
                        AsyncImage(
                            model = novedad.imagenUrl,
                            contentDescription = novedad.nombre,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        )
                        Text(
                            text = novedad.nombre,
                            modifier = Modifier.padding(12.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else {
                Text("Cargando recetas...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(24.dp))

            // 🍴 Categorías
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Categorías", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                TextButton(onClick = { navController.navigate("categorias") }) {
                    Text("Ver todas")
                }
            }
            Spacer(Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(categoriasInicio) { categoria ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        onClick = {
                            viewModel.buscarRecetas(categoria.nombreIngles)
                            navController.navigate("buscar")
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(categoria.emoji, style = MaterialTheme.typography.headlineMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(categoria.nombreEspanol, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}