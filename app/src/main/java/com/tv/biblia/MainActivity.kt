package com.tv.biblia

import android.os.Bundle
import android.os.Process
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tv.biblia.theme.BibliaTVTheme
import com.tv.biblia.ui.AppBiblia
import java.io.File
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val archivoError = File(filesDir, "biblia-crash.txt")
        val manejadorAnterior = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { hilo, error ->
            try {
                archivoError.writeText(
                    "HILO: ${hilo.name}\n\n${Log.getStackTraceString(error)}"
                )
            } catch (_: Throwable) {
            }

            if (manejadorAnterior != null) {
                manejadorAnterior.uncaughtException(hilo, error)
            } else {
                Process.killProcess(Process.myPid())
                exitProcess(10)
            }
        }

        super.onCreate(savedInstanceState)

        val informe = try {
            if (archivoError.exists()) archivoError.readText() else null
        } catch (_: Exception) {
            null
        }

        setContent {
            BibliaTVTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    if (!informe.isNullOrBlank()) {
                        InformeError(informe)
                    } else {
                        AppBiblia()
                    }
                }
            }
        }
    }
}

@Composable
private fun InformeError(informe: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "INFORME DEL FALLO DE BIBLIATV",
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Selecciona y copia el texto siguiente para enviármelo:",
            modifier = Modifier.padding(vertical = 12.dp)
        )
        SelectionContainer {
            Text(text = informe)
        }
    }
}
