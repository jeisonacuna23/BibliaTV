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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tv.biblia.data.Libro
import com.tv.biblia.theme.AzulBarra
import com.tv.biblia.theme.AzulClaro
import com.tv.biblia.theme.AzulPrimario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.Normalizer

private data class ResultadoBusqueda(
    val libro: Libro,
    val capitulo: Int,
    val versiculo: Int,
    val texto: String
)

private fun normalizarBusqueda(texto: String): String {
    return Normalizer.normalize(texto.lowercase(), Normalizer.Form.NFD)
        .replace(Regex("\\p{Mn}+"), "")
        .trim()
}

private fun buscarEnBiblia(
    consulta: String,
    libros: List<Libro>
): List<ResultadoBusqueda> {
    val termino = consulta.trim()
    if (termino.isBlank()) return emptyList()

    val patronCita = Regex("^(.+?)\\s+(\\d+)\\s*:\\s*(\\d+)$")
    val cita = patronCita.matchEntire(termino)

    if (cita != null) {
        val nombreLibro = normalizarBusqueda(cita.groupValues[1])
        val numeroCapitulo = cita.groupValues[2].toIntOrNull() ?: return emptyList()
        val numeroVersiculo = cita.groupValues[3].toIntOrNull() ?: return emptyList()

        val libro = libros.firstOrNull {
            normalizarBusqueda(it.human) == nombreLibro
        } ?: return emptyList()

        val capitulo = libro.chapters.getOrNull(numeroCapitulo - 1)
            ?: return emptyList()

        val item = capitulo.firstOrNull {
            it.esVersiculo && numeroVersiculo in it.verse_numbers
        } ?: return emptyList()

        return listOf(
            ResultadoBusqueda(
                libro = libro,
                capitulo = numeroCapitulo,
                versiculo = numeroVersiculo,
                texto = item.texto
            )
        )
    }

    val terminoNormalizado = normalizarBusqueda(termino)
    if (terminoNormalizado.isBlank()) return emptyList()

    return buildList {
        for (libro in libros) {
            if (size >= 200) break

            for ((indiceCapitulo, capitulo) in libro.chapters.withIndex()) {
                if (size >= 200) break

                for (item in capitulo) {
                    if (size >= 200) break

                    if (!item.esVersiculo) continue

                    if (normalizarBusqueda(item.texto).contains(terminoNormalizado)) {
                        val numero = item.numeroVersiculo ?: continue

                        add(
                            ResultadoBusqueda(
                                libro = libro,
                                capitulo = indiceCapitulo + 1,
                                versiculo = numero,
                                texto = item.texto
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBusqueda(
    libros: List<Libro>,
    onBack: () -> Unit,
    onResultadoClick: (String, Int, Int) -> Unit
) {
    var consulta by remember { mutableStateOf("") }
    var resultados by remember { mutableStateOf(emptyList<ResultadoBusqueda>()) }
    var buscando by remember { mutableStateOf(false) }

    LaunchedEffect(consulta, libros) {
        if (consulta.isBlank()) {
            resultados = emptyList()
            buscando = false
        } else {
            buscando = true
            delay(250)

            val nuevosResultados = withContext(Dispatchers.Default) {
                buscarEnBiblia(consulta, libros)
            }

            resultados = nuevosResultados
            buscando = false
        }
    }

    val esCita = Regex("^(.+?)\\s+(\\d+)\\s*:\\s*(\\d+)$")
        .matches(consulta.trim())

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
                label = { Text("Palabra, frase o cita bíblica") },
                placeholder = { Text("Ej. Juan 16:33") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                }
            )

            Text(
                text = when {
                    consulta.isBlank() ->
                        "Busca palabras, frases o una cita como Juan 16:33."
                    buscando ->
                        "Buscando..."
                    resultados.isEmpty() && esCita ->
                        "No se encontró esa cita. Comprueba el libro, capítulo y versículo."
                    resultados.isEmpty() ->
                        "No se encontraron coincidencias."
                    esCita ->
                        "Cita encontrada"
                    resultados.size >= 200 ->
                        "Se muestran los primeros 200 resultados."
                    else ->
                        "${resultados.size} resultados encontrados"
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
