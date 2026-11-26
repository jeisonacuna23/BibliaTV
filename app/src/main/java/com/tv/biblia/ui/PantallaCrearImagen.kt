package com.tv.biblia.ui

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.tv.biblia.R
import com.tv.biblia.theme.Lora
import com.tv.biblia.theme.Montserrat
import com.tv.biblia.theme.Nunito
import com.tv.biblia.theme.PlayfairDisplay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class FondoImagen(
    val nombre: String,
    val color: Color,
    val colorTexto: Color
)

private data class FuenteImagen(
    val nombre: String,
    val familia: FontFamily,
    val recurso: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearImagen(
    texto: String,
    referencia: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val fondos = listOf(
        FondoImagen("Marfil", Color(0xFFF8F1E7), Color(0xFF342D29)),
        FondoImagen("Rosa", Color(0xFFF5E3E5), Color(0xFF49343A)),
        FondoImagen("Verde", Color(0xFFE4EEE5), Color(0xFF304638)),
        FondoImagen("Azul", Color(0xFFE3EBF3), Color(0xFF304354)),
        FondoImagen("Lavanda", Color(0xFFECE5F2), Color(0xFF40364D)),
        FondoImagen("Arena", Color(0xFFF0E6D5), Color(0xFF493D2E))
    )

    val fuentes = listOf(
        FuenteImagen("Nunito", Nunito, R.font.nunito_regular),
        FuenteImagen("Lora", Lora, R.font.lora),
        FuenteImagen("Playfair", PlayfairDisplay, R.font.playfair_display),
        FuenteImagen("Montserrat", Montserrat, R.font.montserrat)
    )

    var fondoElegido by remember { mutableStateOf(0) }
    var fuenteElegida by remember { mutableStateOf(0) }
    var guardando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("") }

    val fondo = fondos[fondoElegido]
    val fuente = fuentes[fuenteElegida]

    Scaffold(
        containerColor = Color(0xFFF7F8FA),
        topBar = {
            TopAppBar(
                title = { Text("Crear imagen") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "Vista previa",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(fondo.color)
                    .padding(horizontal = 24.dp, vertical = 30.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = texto,
                        color = fondo.colorTexto,
                        fontFamily = fuente.familia,
                        fontSize = 19.sp,
                        lineHeight = 29.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontWeight = FontWeight.Normal
                    )

                    Text(
                        text = referencia,
                        modifier = Modifier.padding(top = 28.dp),
                        color = fondo.colorTexto,
                        fontFamily = fuente.familia,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Text(
                        text = "REINA-VALERA 1960",
                        modifier = Modifier.padding(top = 7.dp),
                        color = fondo.colorTexto.copy(alpha = 0.75f),
                        fontFamily = fuente.familia,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.2.sp
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Fondo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    fondos.take(3).forEachIndexed { index, opcion ->
                        FilterChip(
                            selected = fondoElegido == index,
                            onClick = { fondoElegido = index },
                            label = { Text(opcion.nombre) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(opcion.color)
                                        .padding(7.dp)
                                )
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    fondos.drop(3).forEachIndexed { index, opcion ->
                        val realIndex = index + 3
                        FilterChip(
                            selected = fondoElegido == realIndex,
                            onClick = { fondoElegido = realIndex },
                            label = { Text(opcion.nombre) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(opcion.color)
                                        .padding(7.dp)
                                )
                            }
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Tipografía",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    fuentes.forEachIndexed { index, opcion ->
                        FilterChip(
                            selected = fuenteElegida == index,
                            onClick = { fuenteElegida = index },
                            label = { Text(opcion.nombre) }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        guardando = true
                        mensaje = ""

                        val resultado = withContext(Dispatchers.IO) {
                            guardarImagenBiblica(
                                context = context,
                                texto = texto,
                                referencia = referencia,
                                colorFondo = fondo.color,
                                colorTexto = fondo.colorTexto,
                                fuenteId = fuente.recurso
                            )
                        }

                        guardando = false
                        mensaje = if (resultado != null) {
                            "Imagen guardada en Imágenes/BibliaTV."
                        } else {
                            "No se pudo guardar la imagen. Inténtalo de nuevo."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !guardando,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (guardando) {
                    Text("Guardando imagen...")
                } else {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null
                    )
                    Text(
                        text = "Guardar imagen",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            if (mensaje.isNotEmpty()) {
                Text(
                    text = mensaje,
                    color = if (mensaje.startsWith("Imagen")) {
                        Color(0xFF287A45)
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun guardarImagenBiblica(
    context: android.content.Context,
    texto: String,
    referencia: String,
    colorFondo: Color,
    colorTexto: Color,
    fuenteId: Int
): Uri? {
    val ancho = 1080
    val alto = 1440

    val bitmap = Bitmap.createBitmap(
        ancho,
        alto,
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)

    canvas.drawColor(
        android.graphics.Color.rgb(
            (colorFondo.red * 255).toInt(),
            (colorFondo.green * 255).toInt(),
            (colorFondo.blue * 255).toInt()
        )
    )

    val color = android.graphics.Color.rgb(
        (colorTexto.red * 255).toInt(),
        (colorTexto.green * 255).toInt(),
        (colorTexto.blue * 255).toInt()
    )

    val typeface = ResourcesCompat.getFont(context, fuenteId)
        ?: Typeface.DEFAULT

    val margen = 90
    val anchoTexto = ancho - margen * 2

    val pintura = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        textSize = 49f
        this.typeface = typeface
    }

    var distribucion: StaticLayout

    while (true) {
        distribucion = StaticLayout.Builder
            .obtain(texto, 0, texto.length, pintura, anchoTexto)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .setIncludePad(false)
            .setLineSpacing(12f, 1f)
            .build()

        if (distribucion.height <= 920 || pintura.textSize <= 32f) {
            break
        }

        pintura.textSize -= 2f
    }

    val inicioTexto = ((alto - distribucion.height) / 2f - 35f)
        .coerceAtLeast(100f)

    canvas.save()
    canvas.translate(margen.toFloat(), inicioTexto)
    distribucion.draw(canvas)
    canvas.restore()

    val pinturaReferencia = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        textSize = 43f
        typeface = Typeface.create(typeface, Typeface.BOLD)
        textAlign = Paint.Align.CENTER
    }

    canvas.drawText(
        referencia,
        ancho / 2f,
        1260f,
        pinturaReferencia
    )

    val pinturaVersion = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        alpha = 190
        textSize = 25f
        this.typeface = typeface
        textAlign = Paint.Align.CENTER
        letterSpacing = 2f
    }

    canvas.drawText(
        "REINA-VALERA 1960",
        ancho / 2f,
        1310f,
        pinturaVersion
    )

    val nombre = "BibliaTV_" +
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date()) + ".png"

    val valores = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, nombre)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/BibliaTV"
            )
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }

    val coleccion = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    var uri: Uri? = null

    return try {
        uri = context.contentResolver.insert(coleccion, valores)
            ?: throw IllegalStateException("No se pudo crear el archivo")

        context.contentResolver.openOutputStream(uri)?.use { salida ->
            if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, salida)) {
                throw IllegalStateException("No se pudo generar el PNG")
            }
        } ?: throw IllegalStateException("No se pudo abrir el archivo")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val finalizacion = ContentValues().apply {
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }
            context.contentResolver.update(uri, finalizacion, null, null)
        }

        uri
    } catch (error: Exception) {
        uri?.let {
            context.contentResolver.delete(it, null, null)
        }
        null
    } finally {
        bitmap.recycle()
    }
}
