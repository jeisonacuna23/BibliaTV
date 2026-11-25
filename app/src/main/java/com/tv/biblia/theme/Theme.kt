package com.tv.biblia.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val BibliaColorScheme = lightColorScheme(
    primary = AzulPrimario,
    secondary = AzulSecundario,
    tertiary = AzulPrimario,
    background = AzulClaro,
    surface = AzulClaro
)

@Composable
fun BibliaTVTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BibliaColorScheme,
        content = content
    )
}
