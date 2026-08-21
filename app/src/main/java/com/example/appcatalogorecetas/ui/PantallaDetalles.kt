package com.example.appcatalogorecetas.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.appcatalogorecetas.data.local.Receta
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PantallaDetalles(navController: NavController, viewModel: RecetaViewModel, recetaId: String) {

    val context = LocalContext.current

    var recetaState by remember { mutableStateOf<Receta?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var uriTemporal by remember { mutableStateOf<Uri?>(null) }
    var permisoDenegado by remember { mutableStateOf(false) }

    val favoritas by viewModel.recetasFavoritas.collectAsState()
    val esFavorito = favoritas.any { it.id == recetaId }

    LaunchedEffect(recetaId) {
        cargando = true
        recetaState = viewModel.obtenerDetalle(recetaId)
        cargando = false
    }

    val lanzadorCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito && uriTemporal != null) {
            recetaState?.let { receta ->
                val recetaConFoto = receta.copy(fotoUri = uriTemporal.toString())
                recetaState = recetaConFoto
                viewModel.guardarFavorito(recetaConFoto)
            }
        }
    }

    val lanzadorPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            val nuevaUri = crearUriParaFoto(context)
            uriTemporal = nuevaUri
            lanzadorCamara.launch(nuevaUri)
        } else {
            permisoDenegado = true
        }
    }

    fun abrirCamara() {
        val permisoActual = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permisoActual == PackageManager.PERMISSION_GRANTED) {
            val nuevaUri = crearUriParaFoto(context)
            uriTemporal = nuevaUri
            lanzadorCamara.launch(nuevaUri)
        } else {
            lanzadorPermiso.launch(Manifest.permission.CAMERA)
        }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

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
                    .fillMaxWidth()
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

                Text(text = "Mi versión", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))

                if (receta.fotoUri != null) {
                    AsyncImage(
                        model = Uri.parse(receta.fotoUri),
                        contentDescription = "Mi versión de la receta",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedButton(
                    onClick = { abrirCamara() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (receta.fotoUri == null) "📷 Tomar foto de mi versión" else "📷 Tomar otra foto")
                }

                if (permisoDenegado) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Necesitas dar permiso de cámara para usar esta función. Puedes activarlo en Ajustes del sistema > Apps > AppCatalogoRecetas > Permisos.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

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

private fun crearUriParaFoto(context: android.content.Context): Uri {
    val carpeta = context.getExternalFilesDir("Pictures")
    if (carpeta != null && !carpeta.exists()) carpeta.mkdirs()
    val nombreArchivo = "receta_" +
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) + ".jpg"
    val archivo = File(carpeta, nombreArchivo)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
}