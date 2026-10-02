package com.example.gimnasio

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Objeto utilitario que provee funciones auxiliares globales para la gestión de usuarios
 * y la manipulación de vistas de interfaz.
 */
object Herramientas {

    /**
     * Busca un usuario registrado en la base de datos JSON local basándose en su ID (Long).
     * @param context Contexto de la aplicación utilizado para resolver la ruta del directorio de archivos.
     * @param idBuscar Identificador único (Cédula o ID) del usuario.
     * @return Una representación formateada en `String` con los datos del usuario, o un mensaje informativo de no coincidencia.
     */
    fun buscarUsuarioEnJson(context: Context, idBuscar: Long): String {
        val archivo = File(context.filesDir, "miembros.json")
        val jsonConfig = Json {
            prettyPrint = true
            encodeDefaults = true
        }

        if (archivo.exists() && archivo.readText().isNotBlank()) {
            val contenidoJson = archivo.readText()
            val mapaLeido = jsonConfig.decodeFromString<MutableMap<Long, Usuario>>(contenidoJson)

            // Búsqueda directa por clave dentro del mapa deserializado
            val miembro = mapaLeido[idBuscar]

            if (miembro != null) {
                return """
                        Usuario Encontrado:
                        
                        ${miembro.nombre}
                        
                        ID: ${miembro.id}
                        Edad: ${miembro.edad}
                        Membresía: ${miembro.membresia}
                        Correo: ${miembro.correo}
                        """.trimIndent()
            } else {
                return "Usuario no encontrado"
            }
        } else {
            return "Aún no hay Usuarios Inscritos"
        }
    }
}

/**
 * Función de extensión sobre `Activity` para ocultar fácilmente el teclado virtual suave.
 */
fun Activity.ocultarTeclado() {
    val vista = this.currentFocus
    if (vista != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(vista.windowToken, 0)
    }
}