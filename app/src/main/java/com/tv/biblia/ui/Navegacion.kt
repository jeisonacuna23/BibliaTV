package com.tv.biblia.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tv.biblia.storage.LecturaStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Rutas {
    const val LIBROS = "libros"
    const val CAPITULOS = "capitulos/{usfmLibro}"
    const val BUSQUEDA = "busqueda"
    const val PROVERBIOS = "proverbios"
    const val VERSICULOS = "versiculos/{usfmLibro}/{numeroCapitulo}?verso={verso}"

    fun capitulos(usfm: String) = "capitulos/$usfm"

    fun versiculos(
        usfm: String,
        numero: Int,
        verso: Int? = null
    ) = "versiculos/$usfm/$numero?verso=${verso ?: -1}"
}

@Composable
fun AppBiblia(viewModel: BibliaViewModel = viewModel()) {
    val estado by viewModel.estado.collectAsState()
    val navController = rememberNavController()
    val context = LocalContext.current

    when (val e = estado) {
        is BibliaUiState.Cargando -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is BibliaUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error al cargar la Biblia:\n${e.mensaje}")
            }
        }

        is BibliaUiState.Listo -> {
            val libros = e.libros

            NavHost(
                navController = navController,
                startDestination = Rutas.LIBROS
            ) {
                composable(Rutas.LIBROS) {
                    PantallaLibros(
                        libros = libros,
                        onLibroClick = { libro ->
                            navController.navigate(Rutas.capitulos(libro.usfm))
                        },
                        onSearchClick = {
                            navController.navigate(Rutas.BUSQUEDA)
                        },
                        onProverbioClick = {
                            navController.navigate(Rutas.PROVERBIOS)
                        }
                    )
                }

                composable(Rutas.PROVERBIOS) {
                    val libroProverbios = libros.firstOrNull { it.usfm == "PRO" }

                    if (libroProverbios != null) {
                        PantallaProverbio(
                            libro = libroProverbios,
                            onBack = { navController.popBackStack() }
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No se encontró el libro de Proverbios.")
                        }
                    }
                }

                composable(Rutas.BUSQUEDA) {
                    PantallaBusqueda(
                        libros = libros,
                        onBack = { navController.popBackStack() },
                        onResultadoClick = { usfm, capitulo, verso ->
                            navController.navigate(
                                Rutas.versiculos(usfm, capitulo, verso)
                            )
                        }
                    )
                }

                composable(
                    route = Rutas.CAPITULOS,
                    arguments = listOf(
                        navArgument("usfmLibro") {
                            type = NavType.StringType
                        }
                    )
                ) { backStackEntry ->
                    val usfm = backStackEntry.arguments
                        ?.getString("usfmLibro").orEmpty()

                    val libro = libros.firstOrNull { it.usfm == usfm }

                    if (libro != null) {
                        PantallaCapitulos(
                            libro = libro,
                            onCapituloClick = { num ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    LecturaStore.guardar(context, libro.usfm, num)
                                }

                                navController.navigate(
                                    Rutas.versiculos(libro.usfm, num)
                                )
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }

                composable(
                    route = Rutas.VERSICULOS,
                    arguments = listOf(
                        navArgument("usfmLibro") {
                            type = NavType.StringType
                        },
                        navArgument("numeroCapitulo") {
                            type = NavType.IntType
                        },
                        navArgument("verso") {
                            type = NavType.IntType
                            defaultValue = -1
                        }
                    )
                ) { backStackEntry ->
                    val usfm = backStackEntry.arguments
                        ?.getString("usfmLibro").orEmpty()

                    val num = backStackEntry.arguments
                        ?.getInt("numeroCapitulo") ?: 1

                    val verso = backStackEntry.arguments
                        ?.getInt("verso")?.takeIf { it > 0 }

                    val libro = libros.firstOrNull { it.usfm == usfm }

                    if (libro != null) {
                        PantallaVersiculos(
                            libro = libro,
                            numeroCapitulo = num,
                            numeroVersiculoInicial = verso,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
