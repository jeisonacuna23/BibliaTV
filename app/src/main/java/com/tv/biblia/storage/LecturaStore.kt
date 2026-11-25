package com.tv.biblia.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore("lectura")

object LecturaStore {

    private val LIBRO = stringPreferencesKey("libro")
    private val CAPITULO = intPreferencesKey("capitulo")

    suspend fun guardar(
        context: Context,
        usfm: String,
        capitulo: Int
    ) {
        context.dataStore.edit {
            it[LIBRO] = usfm
            it[CAPITULO] = capitulo
        }
    }

    suspend fun leer(
        context: Context
    ): Pair<String?, Int?> {

        val prefs = context.dataStore.data.first()

        return Pair(
            prefs[LIBRO],
            prefs[CAPITULO]
        )
    }
}
