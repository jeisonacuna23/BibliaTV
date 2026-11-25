package com.tv.biblia.data

import kotlinx.serialization.Serializable

/**
 * Libro completo de la Biblia.
 * Mapea 1:1 la estructura del JSON slim:
 *   { "usfm": "GEN", "human": "Génesis", "chapters": [[...], [...]] }
 */
@Serializable
data class Libro(
    val usfm: String,
    val human: String,
    val chapters: List<List<Item>>
) {
    val totalCapitulos: Int get() = chapters.size
}

/**
 * Un item dentro de un capítulo. Puede ser:
 *  - type = "verse"    -> versículo (trae verse_numbers)
 *  - type = "heading1" -> título de sección (NO trae verse_numbers)
 *  - otros tipos futuros (heading2, etc.)
 */
@Serializable
data class Item(
    val type: String,
    val verse_numbers: List<Int> = emptyList(),
    val lines: List<String> = emptyList()
) {
    val esVersiculo: Boolean get() = type == "verse"
    val esEncabezado: Boolean get() = type.startsWith("heading")

    /** Texto completo uniendo las líneas (poesía viene en varias). */
    val texto: String get() = lines.joinToString("\n")

    /** Primer número de versículo, o null si es encabezado. */
    val numeroVersiculo: Int? get() = verse_numbers.firstOrNull()
}
