package com.tv.biblia.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tv.biblia.data.Libro
import com.tv.biblia.theme.AzulBarra
import com.tv.biblia.theme.AzulClaro
import com.tv.biblia.theme.AzulPrimario
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class VersiculoProverbio(
    val capitulo: Int,
    val numeros: List<Int>,
    val texto: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaProverbio(
    libro: Libro,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val preferencias = remember(context) {
        context.getSharedPreferences(
            "biblia_tv_proverbio",
            android.content.Context.MODE_PRIVATE
        )
    }

    val hoy = remember {
        SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
    }

    val versiculos = remember(libro) {
        libro.chapters.flatMapIndexed { indice, capitulo ->
            capitulo.filter {
                it.esVersiculo &&
                    it.verse_numbers.isNotEmpty() &&
                    it.texto.isNotBlank()
            }.map { item ->
                VersiculoProverbio(
                    capitulo = indice + 1,
                    numeros = item.verse_numbers,
                    texto = item.texto
                )
            }
        }
    }

    var indiceActual by remember(libro, hoy) {
        mutableStateOf(
            preferencias.getInt("indice", -1).takeIf {
                preferencias.getString("fecha", "") == hoy &&
                    it in versiculos.indices
            } ?: if (versiculos.isNotEmpty()) {
                versiculos.indices.random()
            } else {
                -1
            }
        )
    }

    LaunchedEffect(hoy, indiceActual) {
        if (indiceActual in versiculos.indices) {
            preferencias.edit()
                .putString("fecha", hoy)
                .putInt("indice", indiceActual)
                .apply()
        }
    }

    Scaffold(
        containerColor = AzulClaro,
        topBar = {
            TopAppBar(
                title = { Text("Proverbios del día") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulBarra,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "SABIDURÍA PARA CADA DÍA",
                color = AzulPrimario,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (indiceActual in versiculos.indices) {

                val actual = versiculos[indiceActual]

                val referenciaNumeros = if (
                    actual.numeros.size > 1 &&
                    actual.numeros.zipWithNext().all { (a, b) -> b == a + 1 }
                ) {
                    "${actual.numeros.first()}–${actual.numeros.last()}"
                } else {
                    actual.numeros.joinToString(", ")
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.White,
                            RoundedCornerShape(20.dp)
                        )
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "«",
                        color = AzulPrimario,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = actual.texto,
                        color = Color(0xFF172B4D),
                        fontSize = 21.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    Text(
                        text = "Proverbios ${actual.capitulo}:$referenciaNumeros",
                        color = AzulPrimario,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Reina-Valera 1960",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        if (versiculos.size > 1) {
                            indiceActual = versiculos.indices
                                .filter { it != indiceActual }
                                .random()
                        }
                    },
                    enabled = versiculos.size > 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulPrimario
                    )
                ) {
                    Text(
                        text = "Mostrar otro versículo",
                        fontWeight = FontWeight.Bold
                    )
                }

            } else {
                Text(
                    text = "No se encontraron versículos de Proverbios.",
                    color = AzulPrimario,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
