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
 * Pantalla que lee el archivo de miembros y filtra de forma exclusiva
 * las direcciones de correo de aquellos usuarios cuya membresía es "Oro".
 */
class CorreosOroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_correos_oro)

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

        // Lectura del archivo local e inserción filtrada de correos en la UI
        if (archivo.exists() && archivo.readText().isNotBlank()) {
            val contenidoJson = archivo.readText()
            val mapaLeido = jsonConfig.decodeFromString<MutableMap<Long, Usuario>>(contenidoJson)
            var contenedorCorreosOro: String = ""
            val txtCorreosOro = findViewById<TextView>(R.id.txtCorreosOro)

            if (mapaLeido.size > 0) {
                mapaLeido.forEach { id, miembro ->
                    if ("Oro" == miembro.membresia) {
                        contenedorCorreosOro += """
                        ${miembro.correo}
                        
                        
                        """.trimIndent()
                    }
                }
                txtCorreosOro.setText(contenedorCorreosOro)
            } else {
                txtCorreosOro.setText("¡Aún no hay usuarios Inscritos!")
            }
        } else {
            val txtCorreosOro = findViewById<TextView>(R.id.txtCorreosOro)
            txtCorreosOro.setText("¡Aún no hay usuarios Inscritos!")
        }
    }
}