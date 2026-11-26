package com.tv.biblia.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.tv.biblia.R

// Tipografía original de BibliaTV
val Nunito = FontFamily(
    Font(R.font.nunito_regular),
    Font(R.font.nunito_bold, FontWeight.Bold)
)

// Tipografías disponibles para el creador de imágenes
val Lora = FontFamily(
    Font(R.font.lora)
)

val PlayfairDisplay = FontFamily(
    Font(R.font.playfair_display)
)

val Montserrat = FontFamily(
    Font(R.font.montserrat)
)

// Tipografía general de la aplicación: se conserva Nunito
val BibliaTypography = Typography(

    titleLarge = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),

    titleMedium = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),

    bodyLarge = TextStyle(
        fontFamily = Nunito,
        fontSize = 20.sp,
        lineHeight = 34.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = Nunito,
        fontSize = 18.sp
    )
)
