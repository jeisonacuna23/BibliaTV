package com.tv.biblia.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tv.biblia.data.Libro
import com.tv.biblia.theme.AzulBarra
import com.tv.biblia.theme.AzulClaro
import com.tv.biblia.theme.AzulPrimario

private data class ResultadoBusqueda(
    val libro: Libro,
    val capitulo: Int,
    val versiculo: Int,
    val texto: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBusqueda(
    libros: List<Libro>,
    onBack: () -> Unit,
    onResultadoClick: (String, Int, Int) -> Unit
) {
    var consulta by remember { mutableStateOf("") }

    val resultados = remember(consulta, libros) {
        val termino = consulta.trim()
        if (termino.isBlank()) {
            emptyList()
        } else {
            buildList {
                for (libro in libros) {
                    for ((indiceCapitulo, capitulo) in libro.chapters.withIndex()) {
                        for (item in capitulo) {
                            if (
                                item.esVersiculo &&
                                item.texto.contains(termino, ignoreCase = true)
                            ) {
                                val numero = item.numeroVersiculo ?: continue
                                add(
                                    ResultadoBusqueda(
                                        libro,
                                        indiceCapitulo + 1,
                                        numero,
                                        item.texto
                                    )
                                )
                                if (size >= 200) break
                            }
                        }
                        if (size >= 200) break
                    }
                    if (size >= 200) break
                }
            }
        }
    }

    Scaffold(
        containerColor = AzulClaro,
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
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                value = consulta,
                onValueChange = { consulta = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Palabra o frase") },
                placeholder = { Text("Ej. En el principio") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                }
            )

            Text(
                text = when {
                    consulta.isBlank() -> "Escribe algo para buscar en los 66 libros."
                    resultados.isEmpty() -> "No se encontraron coincidencias."
                    resultados.size >= 200 -> "Se muestran los primeros 200 resultados."
                    else -> "${resultados.size} resultados encontrados"
                },
                color = AzulPrimario,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(resultados) { resultado ->
                    ListItem(
                        headlineContent = {
                            Text(
                                "${resultado.libro.human} ${resultado.capitulo}:${resultado.versiculo}",
                                color = AzulPrimario
                            )
                        },
                        supportingContent = {
                            Text(resultado.texto)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onResultadoClick(
                                    resultado.libro.usfm,
                                    resultado.capitulo,
                                    resultado.versiculo
                                )
                            }
                    )
                }
            }
        }
    }
}
