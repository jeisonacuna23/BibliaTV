package com.tv.biblia.data

data class ResultadoBusqueda(
    val libro: String,
    val usfm: String,
    val capitulo: Int,
    val versiculo: Int,
    val texto: String
)
