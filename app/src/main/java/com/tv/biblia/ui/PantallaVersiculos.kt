package com.tv.biblia.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tv.biblia.data.Item
import com.tv.biblia.data.Libro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaVersiculos(
    libro: Libro,
    numeroCapitulo: Int,
    onBack: () -> Unit
) {
    val items = libro.chapters.getOrNull(numeroCapitulo - 1).orEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${libro.human} $numeroCapitulo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                RenderItem(item)
            }
        }
    }
}

@Composable
private fun RenderItem(item: Item) {
    when {
        item.esEncabezado -> {
            Text(
                text = item.texto,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
            )
        }
        item.esVersiculo -> {
            Row {
                Text(
                    text = "${item.numeroVersiculo} ",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp
                )
                Text(
                    text = item.texto,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        else -> {
            if (item.lines.isNotEmpty()) {
                Text(
                    text = item.texto,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
