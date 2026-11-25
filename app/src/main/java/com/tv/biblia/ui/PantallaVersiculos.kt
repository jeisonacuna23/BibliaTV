package com.tv.biblia.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tv.biblia.data.Item
import com.tv.biblia.data.Libro
import com.tv.biblia.theme.AzulBarra
import com.tv.biblia.theme.AzulClaro
import com.tv.biblia.theme.AzulPrimario
import com.tv.biblia.theme.AzulSeleccion

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PantallaVersiculos(
    libro: Libro,
    numeroCapitulo: Int,
    onBack: () -> Unit,
    numeroVersiculoInicial: Int? = null
) {
    val items = libro.chapters.getOrNull(numeroCapitulo - 1).orEmpty()
    val clipboard = LocalClipboardManager.current
    val listaState = rememberLazyListState()
    var seleccionados by remember { mutableStateOf(setOf<Int>()) }

    LaunchedEffect(numeroVersiculoInicial, items) {
        if (numeroVersiculoInicial != null) {
            val indice = items.indexOfFirst {
                it.esVersiculo && it.numeroVersiculo == numeroVersiculoInicial
            }
            if (indice >= 0) listaState.scrollToItem(indice)
        }
    }

    val textoSeleccionado = items
        .filter {
            it.esVersiculo && it.numeroVersiculo?.let { numero -> numero in seleccionados } == true
        }
        .joinToString("\n\n") {
            "${libro.human} $numeroCapitulo:${it.numeroVersiculo}\n${it.texto}"
        }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (seleccionados.isEmpty()) {
                            "${libro.human} $numeroCapitulo"
                        } else {
                            "${seleccionados.size} seleccionados"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                },
                actions = {
                    if (seleccionados.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                clipboard.setText(
                                    AnnotatedString(textoSeleccionado)
                                )
                                seleccionados = emptySet()
                            }
                        ) {
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "Copiar versículos"
                            )
                        }
                        IconButton(onClick = { seleccionados = emptySet() }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cancelar selección"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulBarra,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            state = listaState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                horizontal = 20.dp,
                vertical = 18.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(items) { _, item ->
                RenderItem(
                    item = item,
                    seleccionado = item.numeroVersiculo?.let { it in seleccionados } == true,
                    onClick = {
                        val numero = item.numeroVersiculo
                        if (item.esVersiculo && numero != null &&
                            seleccionados.isNotEmpty()
                        ) {
                            seleccionados = if (numero in seleccionados) {
                                seleccionados - numero
                            } else {
                                seleccionados + numero
                            }
                        }
                    },
                    onLongClick = {
                        val numero = item.numeroVersiculo
                        if (item.esVersiculo && numero != null) {
                            seleccionados = seleccionados + numero
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RenderItem(
    item: Item,
    seleccionado: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    when {
        item.esEncabezado -> {
            Text(
                text = item.texto.uppercase(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = AzulPrimario,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
        }

        item.esVersiculo -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (seleccionado) AzulSeleccion else Color.Transparent
                    )
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${item.numeroVersiculo ?: ""} ",
                    fontWeight = FontWeight.Bold,
                    color = AzulPrimario,
                    fontSize = 14.sp
                )
                Text(
                    text = item.texto,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 32.sp,
                    color = Color(0xFF172B4D)
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
