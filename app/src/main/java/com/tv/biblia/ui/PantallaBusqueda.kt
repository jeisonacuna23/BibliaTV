package com.tv.biblia.ui

import androidx.compose.ui.draw.clip

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.background

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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.tv.biblia.data.Libro
import com.tv.biblia.theme.AzulBarra
import com.tv.biblia.theme.AzulClaro
import com.tv.biblia.theme.AzulPrimario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.Normalizer
import java.util.Locale

private enum class ModoBusqueda {
    PALABRA,
    CITA
}

private data class ResultadoBusqueda(
    val libro: Libro,
    val capitulo: Int,
    val versiculo: Int,
    val texto: String
)

private data class GrupoResultado(
    val libro: Libro,
    val resultados: List<ResultadoBusqueda>
)

private data class TextoNormalizado(
    val texto: String,
    val inicios: List<Int>,
    val finales: List<Int>
)

private fun normalizarBusqueda(texto: String): String {
    return Normalizer.normalize(
        texto.lowercase(Locale.ROOT),
        Normalizer.Form.NFD
    ).replace(Regex("\\p{Mn}+"), "").trim()
}

private fun normalizarConIndices(texto: String): TextoNormalizado {
    val normalizado = StringBuilder()
    val inicios = mutableListOf<Int>()
    val finales = mutableListOf<Int>()

    var indice = 0

    while (indice < texto.length) {
        val codigo = texto.codePointAt(indice)
        val longitud = Character.charCount(codigo)
        val original = String(Character.toChars(codigo))

        val descompuesto = Normalizer.normalize(
            original.lowercase(Locale.ROOT),
            Normalizer.Form.NFD
        )

        for (caracter in descompuesto) {
            val tipo = Character.getType(caracter)

            if (
                tipo != Character.NON_SPACING_MARK.toInt() &&
                tipo != Character.COMBINING_SPACING_MARK.toInt() &&
                tipo != Character.ENCLOSING_MARK.toInt()
            ) {
                normalizado.append(caracter)
                inicios.add(indice)
                finales.add(indice + longitud)
            }
        }

        indice += longitud
    }

    return TextoNormalizado(
        texto = normalizado.toString(),
        inicios = inicios,
        finales = finales
    )
}

private fun buscarPorPalabra(
    consulta: String,
    libros: List<Libro>
): List<ResultadoBusqueda> {
    val termino = normalizarBusqueda(consulta)

    if (termino.isBlank()) return emptyList()

    return buildList {
        for (libro in libros) {
            for ((indiceCapitulo, capitulo) in libro.chapters.withIndex()) {
                for (item in capitulo) {
                    if (size >= 200) return@buildList

                    if (!item.esVersiculo) continue

                    if (normalizarBusqueda(item.texto).contains(termino)) {
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

private fun buscarPorCita(
    consulta: String,
    libros: List<Libro>
): List<ResultadoBusqueda> {
    val patron = Regex("^(.+?)\\s+(\\d+)\\s*:\\s*(\\d+)$")
    val coincidencia = patron.matchEntire(consulta.trim())
        ?: return emptyList()

    val nombreLibro = normalizarBusqueda(coincidencia.groupValues[1])
    val numeroCapitulo = coincidencia.groupValues[2].toIntOrNull()
        ?: return emptyList()
    val numeroVersiculo = coincidencia.groupValues[3].toIntOrNull()
        ?: return emptyList()

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

private fun textoConResaltado(
    texto: String,
    consulta: String
): AnnotatedString {
    val termino = normalizarBusqueda(consulta)

    if (termino.isBlank()) return AnnotatedString(texto)

    val normalizado = normalizarConIndices(texto)
    val rangos = mutableListOf<Pair<Int, Int>>()

    var buscarDesde = 0

    while (buscarDesde < normalizado.texto.length) {
        val inicio = normalizado.texto.indexOf(termino, buscarDesde)

        if (inicio < 0) break

        val ultimo = inicio + termino.length - 1

        if (ultimo >= normalizado.finales.size) break

        rangos.add(
            normalizado.inicios[inicio] to normalizado.finales[ultimo]
        )

        buscarDesde = inicio + termino.length
    }

    if (rangos.isEmpty()) return AnnotatedString(texto)

    return buildAnnotatedString {
        var cursor = 0

        for ((inicio, final) in rangos) {
            if (inicio < cursor) continue

            append(texto.substring(cursor, inicio))

            withStyle(
                SpanStyle(
                    background = AzulBarra,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(texto.substring(inicio, final))
            }

            cursor = final
        }

        append(texto.substring(cursor))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBusqueda(
    libros: List<Libro>,
    onBack: () -> Unit,
    onResultadoClick: (String, Int, Int) -> Unit
) {
    var modo by remember { mutableStateOf(ModoBusqueda.PALABRA) }
    var consulta by remember { mutableStateOf("") }
    var resultados by remember {
        mutableStateOf(emptyList<ResultadoBusqueda>())
    }
    var buscando by remember { mutableStateOf(false) }
    var librosExpandidos by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(consulta, libros, modo) {
        if (consulta.isBlank()) {
            resultados = emptyList()
            buscando = false
        } else {
            buscando = true
            delay(250)

            val nuevosResultados = withContext(Dispatchers.Default) {
                when (modo) {
                    ModoBusqueda.PALABRA ->
                        buscarPorPalabra(consulta, libros)

                    ModoBusqueda.CITA ->
                        buscarPorCita(consulta, libros)
                }
            }

            resultados = nuevosResultados
            buscando = false
        }
    }

    val grupos = resultados
        .groupBy { it.libro.usfm }
        .values
        .map { grupo ->
            GrupoResultado(
                libro = grupo.first().libro,
                resultados = grupo
            )
        }

    val citaValida = Regex("^(.+?)\\s+(\\d+)\\s*:\\s*(\\d+)$")
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
            TabRow(
                selectedTabIndex = if (modo == ModoBusqueda.PALABRA) 0 else 1
            ) {
                Tab(
                    selected = modo == ModoBusqueda.PALABRA,
                    onClick = {
                        modo = ModoBusqueda.PALABRA
                        consulta = ""
                        resultados = emptyList()
                    },
                    text = { Text("Por palabra") }
                )

                Tab(
                    selected = modo == ModoBusqueda.CITA,
                    onClick = {
                        modo = ModoBusqueda.CITA
                        consulta = ""
                        resultados = emptyList()
                    },
                    text = { Text("Por cita") }
                )
            }

            OutlinedTextField(
                value = consulta,
                onValueChange = { consulta = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                singleLine = true,
                label = {
                    Text(
                        if (modo == ModoBusqueda.PALABRA) {
                            "Palabra o frase"
                        } else {
                            "Cita bíblica"
                        }
                    )
                },
                placeholder = {
                    Text(
                        if (modo == ModoBusqueda.PALABRA) {
                            "Ej. ángel"
                        } else {
                            "Ej. Juan 3:16"
                        }
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                }
            )

            Text(
                text = when {
                    consulta.isBlank() && modo == ModoBusqueda.PALABRA ->
                        "Busca una palabra o frase en toda la Biblia."

                    consulta.isBlank() ->
                        "Escribe una referencia como Juan 3:16."

                    buscando ->
                        "Buscando..."

                    modo == ModoBusqueda.CITA && !citaValida ->
                        "Escribe la cita con el formato Libro capítulo:versículo."

                    resultados.isEmpty() ->
                        "No se encontraron coincidencias."

                    modo == ModoBusqueda.CITA ->
                        "Cita encontrada."

                    resultados.size >= 200 ->
                        "Se muestran los primeros 200 resultados."

                    else ->
                        "${resultados.size} coincidencias en ${grupos.size} libros."
                },
                color = AzulPrimario,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(
                    items = grupos,
                    key = { it.libro.usfm }
                ) { grupo ->
                    val expandido = grupo.libro.usfm in librosExpandidos

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    librosExpandidos =
                                        if (expandido) {
                                            librosExpandidos - grupo.libro.usfm
                                        } else {
                                            librosExpandidos + grupo.libro.usfm
                                        }
                                }
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${grupo.libro.human} · ${grupo.resultados.size} coincidencias",
                                color = AzulPrimario,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (expandido) "−" else "+",
                                color = AzulPrimario,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (expandido) {
                            grupo.resultados.forEach { resultado ->
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            "${resultado.libro.human} ${resultado.capitulo}:${resultado.versiculo}",
                                            color = AzulPrimario
                                        )
                                    },
                                    supportingContent = {
                                        Text(
                                            if (modo == ModoBusqueda.PALABRA) {
                                                textoConResaltado(resultado.texto, consulta)
                                            } else {
                                                AnnotatedString(resultado.texto)
                                            }
                                        )
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
        }
    }
}
