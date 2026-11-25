package com.tv.biblia.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.tv.biblia.data.Item
import com.tv.biblia.data.Libro

private val AzulPastel = Color(0xFF6C9DFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaVersiculos(
    libro: Libro,
    numeroCapitulo: Int,
    onBack: () -> Unit,
    numeroVersiculoInicial: Int? = null
) {
    val contenidoCapitulo =
        libro.chapters.getOrNull(numeroCapitulo - 1).orEmpty()

    val indiceVersiculo = numeroVersiculoInicial?.let { numero ->
        contenidoCapitulo.indexOfFirst { item ->
            item.esVersiculo && numero in item.verse_numbers
        }
    } ?: -1

    val listState = rememberLazyListState()

    LaunchedEffect(indiceVersiculo) {
        if (indiceVersiculo >= 0) {
            listState.scrollToItem(indiceVersiculo)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${libro.human} $numeroCapitulo",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                horizontal = 24.dp,
                vertical = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(contenidoCapitulo) { _, item ->
                val destacado =
                    numeroVersiculoInicial != null &&
                    item.esVersiculo &&
                    numeroVersiculoInicial in item.verse_numbers

                RenderItem(
                    item = item,
                    destacado = destacado
                )
            }
        }
    }
}

@Composable
private fun RenderItem(
    item: Item,
    destacado: Boolean = false
) {
    when {

        item.esEncabezado -> {
            Text(
                text = item.texto.uppercase(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = AzulPastel,
                modifier = Modifier.padding(
                    top = 16.dp,
                    bottom = 8.dp
                )
            )
        }

        item.esVersiculo -> {
            Row(
                modifier = if (destacado) {
                    Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                } else {
                    Modifier
                }
            ) {
                Text(
                    text = "${item.numeroVersiculo} ",
                    fontWeight = FontWeight.Bold,
                    color = AzulPastel,
                    fontSize = 14.sp
                )

                Text(
                    text = item.texto,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 32.sp
                )
            }
        }

        else -> {
            if (item.lines.isNotEmpty()) {
                Text(
                    text = item.texto,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
