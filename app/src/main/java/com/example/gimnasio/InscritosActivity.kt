package com.example.gimnasio

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Pantalla encargada de listar la totalidad de miembros registrados
 * leyéndolos desde el archivo local `miembros.json`.
 */
class InscritosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inscritos)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val archivo = File(this.filesDir, "miembros.json")
        val jsonConfig = Json {
            prettyPrint = true
            encodeDefaults = true
        }

        // Comprobación de existencia del archivo y disponibilidad de registros
        if (archivo.exists() && archivo.readText().isNotBlank()) {
            val contenidoJson = archivo.readText()
            val mapaLeido = jsonConfig.decodeFromString<MutableMap<Long, Usuario>>(contenidoJson)

            if (mapaLeido.size < 1) {
                val txtMostrarUsuarios = findViewById<TextView>(R.id.txtMostrarUsuariosInscritos)
                txtMostrarUsuarios.setText("¡Aún no hay usuarios Inscritos!")
            } else {
                mostrarUsuariosInscritos()
            }
        } else {
            val txtMostrarUsuarios = findViewById<TextView>(R.id.txtMostrarUsuariosInscritos)
            txtMostrarUsuarios.setText("¡Aún no hay usuarios Inscritos!")
        }
    }

    /**
     * Parsea el mapa completo de usuarios y formatea los datos en una lista legible dentro de un TextView.
     */
    fun mostrarUsuariosInscritos() {
        val txtMostrarUsuarios = findViewById<TextView>(R.id.txtMostrarUsuariosInscritos)
        val archivo = File(this.filesDir, "miembros.json")
        val jsonConfig = Json {
            prettyPrint = true
            encodeDefaults = true
        }
        val contenidoJson = archivo.readText()

        val mapaLeido = jsonConfig.decodeFromString<MutableMap<Long, Usuario>>(contenidoJson)
        var contador: Int = 0
        var contenedor: String = ""

        mapaLeido.forEach { (id, miembro) ->
            contador++
            contenedor += """
                    Usuario $contador${miembro.nombre}:

                    ID: ${miembro.id}
                    Edad: ${miembro.edad}
                    Membresía: ${miembro.membresia}
                    Correo: ${miembro.correo}
                    
                    
                    
                    
                    """.trimIndent()
        }
        txtMostrarUsuarios.setText(contenedor)
    }
}