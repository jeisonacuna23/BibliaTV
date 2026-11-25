package com.tv.biblia.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val BibliaColorScheme = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = AzulSecundario,
    tertiary = AzulPrimario,
    background = AzulClaro,
    onBackground = TextoOscuro,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = TextoOscuro,
    surfaceVariant = AzulClaro,
    onSurfaceVariant = TextoOscuro
)

@Composable
fun BibliaTVTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BibliaColorScheme,
        typography = BibliaTypography,
        content = content
    )
}
