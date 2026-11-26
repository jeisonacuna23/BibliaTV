package com.tv.biblia.ui

import android.app.Application
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tv.biblia.data.Libro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

sealed interface BibliaUiState {
    data object Cargando : BibliaUiState
    data class Listo(val libros: List<Libro>) : BibliaUiState
    data class Error(val mensaje: String) : BibliaUiState
}

class BibliaViewModel(app: Application) : AndroidViewModel(app) {

    private val _estado = MutableStateFlow<BibliaUiState>(BibliaUiState.Cargando)
    val estado: StateFlow<BibliaUiState> = _estado.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true }

    init {
        cargarBiblia()
    }

    private fun cargarBiblia() {
        viewModelScope.launch {
            val inicioTotal = SystemClock.elapsedRealtime()

            try {
                val inicioLectura = SystemClock.elapsedRealtime()

                val texto = withContext(Dispatchers.IO) {
                    getApplication<Application>().assets
                        .open("biblia_slim.json")
                        .bufferedReader()
                        .use { it.readText() }
                }

                val tiempoLectura = SystemClock.elapsedRealtime() - inicioLectura

                val inicioConversion = SystemClock.elapsedRealtime()

                val libros = withContext(Dispatchers.Default) {
                    json.decodeFromString<List<Libro>>(texto)
                }

                val tiempoConversion = SystemClock.elapsedRealtime() - inicioConversion
                val tiempoTotal = SystemClock.elapsedRealtime() - inicioTotal

                Log.i(
                    "BibliaTV_Load",
                    "JSON leído: ${tiempoLectura} ms | " +
                        "Conversión: ${tiempoConversion} ms | " +
                        "Total: ${tiempoTotal} ms | " +
                        "Libros: ${libros.size} | " +
                        "Tamaño: ${texto.length} caracteres"
                )

                _estado.value = BibliaUiState.Listo(libros)

            } catch (e: Throwable) {
                Log.e("BibliaTV_Load", "Error cargando la Biblia", e)

                _estado.value = BibliaUiState.Error(
                    "${e.javaClass.simpleName}: ${e.message ?: "Error sin mensaje"}"
                )
            }
        }
    }
}
