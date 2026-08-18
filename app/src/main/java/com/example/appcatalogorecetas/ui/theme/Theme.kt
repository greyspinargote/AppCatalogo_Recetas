package com.example.appcatalogorecetas.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = VerdeOscuro,
    onPrimary = Color.White,
    primaryContainer = VerdeContenedor,
    onPrimaryContainer = VerdeOscuro,
    secondary = VerdeClaro,
    onSecondary = Color.White,
    tertiary = CremaOscuro,
    onTertiary = TextoOscuro,
    tertiaryContainer = CremaOscuro,
    onTertiaryContainer = TextoOscuro,
    background = Crema,
    onBackground = TextoOscuro,
    surface = Color.White,
    onSurface = TextoOscuro,
    error = Color(0xFFB3261E),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = VerdeClaro,
    onPrimary = Color.Black,
    primaryContainer = VerdeOscuro,
    onPrimaryContainer = Color.White,
    secondary = VerdeClaro,
    onSecondary = Color.Black,
    tertiary = SuperficieOscura,
    onTertiary = Color.White,
    tertiaryContainer = SuperficieOscura,
    onTertiaryContainer = Color.White,
    background = FondoOscuro,
    onBackground = Color.White,
    surface = SuperficieOscura,
    onSurface = Color.White,
    error = Color(0xFFF2B8B5),
    onError = Color.Black
)

@Composable
fun AppCatalogoRecetasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}