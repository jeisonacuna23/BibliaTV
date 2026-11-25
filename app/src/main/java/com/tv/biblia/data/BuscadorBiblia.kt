package com.tv.biblia.data

import java.text.Normalizer
import java.util.Locale

object BuscadorBiblia {

    private fun normalizar(texto: String): String {
        return Normalizer.normalize(
            texto.lowercase(Locale.ROOT),
            Normalizer.Form.NFD
        )
            .replace(Regex("\\p{Mn}+"), "")
            .trim()
    }

    fun buscar(
        libros: List<Libro>,
        consulta: String
    ): List<ResultadoBusqueda> {

        val termino = normalizar(consulta)

        if (termino.isBlank()) return emptyList()

        val resultados = mutableListOf<ResultadoBusqueda>()

        libros.forEach { libro ->

            libro.chapters.forEachIndexed { indiceCapitulo, items ->

                items.forEach { item ->

                    if (item.esVersiculo &&
                        normalizar(item.texto).contains(termino)
                    ) {
                        item.verse_numbers.forEach { numero ->

                            resultados.add(
                                ResultadoBusqueda(
                                    libro = libro.human,
                                    usfm = libro.usfm,
                                    capitulo = indiceCapitulo + 1,
                                    versiculo = numero,
                                    texto = item.texto
                                )
                            )
                        }
                    }
                }
            }
        }

        return resultados
    }
}
