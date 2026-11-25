package com.tv.biblia.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tv.biblia.data.Libro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLibros(
    libros: List<Libro>,
    onLibroClick: (Libro) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("BibliaTV") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(libros) { libro ->
                ListItem(
                    headlineContent = { Text(libro.human) },
                    supportingContent = { Text("${libro.totalCapitulos} capítulos") },
                    modifier = Modifier.clickable { onLibroClick(libro) }
                )
                HorizontalDivider()
            }
        }
    }
}
