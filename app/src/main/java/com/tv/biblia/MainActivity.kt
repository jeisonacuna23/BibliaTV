package com.tv.biblia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tv.biblia.ui.BibliaUiState
import com.tv.biblia.ui.BibliaViewModel
import com.tv.biblia.ui.PantallaLibros

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppBiblia()
                }
            }
        }
    }
}

@Composable
fun AppBiblia(viewModel: BibliaViewModel = viewModel()) {
    val estado by viewModel.estado.collectAsState()

    when (val e = estado) {
        is BibliaUiState.Cargando -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is BibliaUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error al cargar la Biblia:\n${e.mensaje}")
            }
        }
        is BibliaUiState.Listo -> {
            PantallaLibros(
                libros = e.libros,
                onLibroClick = { /* Paso 1.C conectará la navegación */ }
            )
        }
    }
}
