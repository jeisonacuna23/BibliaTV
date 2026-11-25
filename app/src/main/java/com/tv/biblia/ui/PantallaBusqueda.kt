package com.tv.biblia.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tv.biblia.data.BuscadorBiblia
import com.tv.biblia.data.Libro
import com.tv.biblia.data.ResultadoBusqueda
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBusqueda(
    libros: List<Libro>,
    onResultadoClick: (ResultadoBusqueda) -> Unit,
    onBack: () -> Unit
) {
    var consulta by remember { mutableStateOf("") }
    var resultados by remember {
        mutableStateOf<List<ResultadoBusqueda>>(emptyList())
    }
    var buscando by remember { mutableStateOf(false) }

    LaunchedEffect(consulta, libros) {
        if (consulta.isBlank()) {
            resultados = emptyList()
            buscando = false
        } else {
            buscando = true
            delay(250)

            val encontrados = withContext(Dispatchers.Default) {
                BuscadorBiblia.buscar(libros, consulta)
            }

            resultados = encontrados
            buscando = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buscar en la Biblia") },
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = consulta,
                onValueChange = { consulta = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = {
                    Text("Escribe una palabra o frase...")
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                },
                trailingIcon = {
                    if (consulta.isNotEmpty()) {
                        IconButton(onClick = { consulta = "" }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Limpiar búsqueda"
                            )
                        }
                    }
                },
                singleLine = true
            )

            when {
                consulta.isBlank() -> {
                    MensajeBusqueda(
                        texto = "Busca palabras o frases en los 66 libros de la Biblia.",
                        modifier = Modifier.weight(1f)
                    )
                }

                buscando -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                resultados.isEmpty() -> {
                    MensajeBusqueda(
                        texto = "No se encontraron coincidencias.",
                        modifier = Modifier.weight(1f)
                    )
                }

                else -> {
                    Text(
                        text = "${resultados.size} resultados encontrados",
                        modifier = Modifier.padding(
                            horizontal = 20.dp,
                            vertical = 8.dp
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = resultados,
                            key = {
                                "${it.usfm}-${it.capitulo}-${it.versiculo}"
                            }
                        ) { resultado ->

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onResultadoClick(resultado)
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "${resultado.libro} ${resultado.capitulo}:${resultado.versiculo}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = resultado.texto,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MensajeBusqueda(
    texto: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(24.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
