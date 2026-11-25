package com.tv.biblia.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tv.biblia.data.Libro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val texto = getApplication<Application>().assets
                    .open("biblia_slim.json")
                    .bufferedReader()
                    .use { it.readText() }

                val libros = json.decodeFromString<List<Libro>>(texto)
                _estado.value = BibliaUiState.Listo(libros)
            } catch (e: Exception) {
                _estado.value = BibliaUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
