package com.tv.biblia.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tv.biblia.data.Libro
import com.tv.biblia.theme.AzulBarra
import com.tv.biblia.theme.AzulClaro
import com.tv.biblia.theme.AzulPrimario

private data class GrupoLibros(
    val titulo: String,
    val codigos: List<String>
)

private val gruposAntiguo = listOf(
    GrupoLibros("Pentateuco", listOf("GEN", "EXO", "LEV", "NUM", "DEU")),
    GrupoLibros("Históricos", listOf("JOS", "JDG", "RUT", "1SA", "2SA", "1KI", "2KI", "1CH", "2CH", "EZR", "NEH", "EST")),
    GrupoLibros("Poéticos", listOf("JOB", "PSA", "PRO", "ECC", "SNG")),
    GrupoLibros("Profetas mayores", listOf("ISA", "JER", "LAM", "EZK", "DAN")),
    GrupoLibros("Profetas menores", listOf("HOS", "JOL", "AMO", "OBA", "JON", "MIC", "NAM", "HAB", "ZEP", "HAG", "ZEC", "MAL"))
)

private val gruposNuevo = listOf(
    GrupoLibros("Evangelios", listOf("MAT", "MRK", "LUK", "JHN")),
    GrupoLibros("Histórico", listOf("ACT")),
    GrupoLibros("Epístolas", listOf("ROM", "1CO", "2CO", "GAL", "EPH", "PHP", "COL", "1TH", "2TH", "1TI", "2TI", "TIT", "PHM", "HEB", "JAS", "1PE", "2PE", "1JN", "2JN", "3JN", "JUD")),
    GrupoLibros("Profético", listOf("REV"))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLibros(
    libros: List<Libro>,
    onLibroClick: (Libro) -> Unit,
    onSearchClick: () -> Unit,
    onProverbioClick: () -> Unit
) {
    var testamentoSeleccionado by remember { mutableStateOf(0) }
    var menuAbierto by remember { mutableStateOf(false) }

    val grupos = if (testamentoSeleccionado == 0) {
        gruposAntiguo
    } else {
        gruposNuevo
    }

    val librosPorCodigo = libros.associateBy { it.usfm }

    Scaffold(
        containerColor = AzulClaro,
        topBar = {
            TopAppBar(
                title = { Text("BibliaTV") },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Buscar en la Biblia"
                        )
                    }

                    Box {
                        IconButton(onClick = { menuAbierto = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Más opciones"
                            )
                        }

                        DropdownMenu(
                            expanded = menuAbierto,
                            onDismissRequest = { menuAbierto = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text("Versículo de Proverbios")
                                },
                                onClick = {
                                    menuAbierto = false
                                    onProverbioClick()
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulBarra,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AzulBarra)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "Antiguo Testamento",
                    "Nuevo Testamento"
                ).forEachIndexed { index, titulo ->

                    val seleccionado = testamentoSeleccionado == index

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (seleccionado) Color.White
                                else Color.Transparent
                            )
                            .clickable {
                                testamentoSeleccionado = index
                            }
                            .padding(horizontal = 4.dp, vertical = 13.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = titulo,
                            color = if (seleccionado) AzulPrimario else Color.White,
                            fontWeight = if (seleccionado) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Medium
                            },
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                grupos.forEach { grupo ->

                    val librosDelGrupo = grupo.codigos.mapNotNull {
                        librosPorCodigo[it]
                    }

                    if (librosDelGrupo.isNotEmpty()) {

                        item(key = "grupo_${grupo.titulo}") {
                            Text(
                                text = grupo.titulo,
                                color = AzulPrimario,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 18.dp,
                                    bottom = 8.dp
                                )
                            )
                        }

                        items(
                            items = librosDelGrupo,
                            key = { it.usfm }
                        ) { libro ->

                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = libro.human,
                                        color = AzulPrimario
                                    )
                                },
                                modifier = Modifier.clickable {
                                    onLibroClick(libro)
                                }
                            )

                            HorizontalDivider(
                                color = Color(0xFFDCE8F5)
                            )
                        }
                    }
                }
            }
        }
    }
}
