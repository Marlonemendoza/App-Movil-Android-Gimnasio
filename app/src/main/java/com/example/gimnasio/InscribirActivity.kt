package com.example.gimnasio

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.button.MaterialButton
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.String

/**
 * Modelo de datos serializable que representa la estructura de un usuario en el gimnasio.
 * @property nombre Nombre completo del miembro.
 * @property id Identificador o número de cédula único (Long).
 * @property edad Edad numérica en años.
 * @property membresia Tipo de paquete asignado ("Bronce", "Plata", "Oro").
 * @property correo Correo de contacto generado automáticamente.
 */
@Serializable
data class Usuario(
    val nombre: String,
    val id: Long,
    val edad: Int,
    val membresia: String,
    val correo: String
)

/**
 * Pantalla que permite registrar nuevos miembros en el sistema
 * y persistirlos en un archivo local `miembros.json`.
 */
class InscribirActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inscribir)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar menú desplegable para membresías
        val opcionesMembresia = arrayOf("Bronce", "Plata", "Oro")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, opcionesMembresia)
        val dropdown = findViewById<AutoCompleteTextView>(R.id.etxtmembresia)
        dropdown.setAdapter(adapter)

        dropdown.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                ocultarTeclado()
                dropdown.showDropDown()
            }
        }

        // Validación dinámica de Edad (16 - 70 años)
        val etxtEdad = findViewById<EditText>(R.id.etxtedad)
        val txtErrorEdad = findViewById<TextView>(R.id.txtErrorEdad)

        etxtEdad.doOnTextChanged { text, start, before, count ->
            val edadTexto = text.toString()

            if (edadTexto.isNotEmpty()) {
                val edad = edadTexto.toIntOrNull()

                if (edad == null) {
                    txtErrorEdad.text = "¡Ingrese solo números!"
                    txtErrorEdad.visibility = View.VISIBLE
                } else if (edad <= 15) {
                    txtErrorEdad.text = "¡Minimo 16 Años!"
                    txtErrorEdad.visibility = View.VISIBLE
                } else if (edad > 70) {
                    txtErrorEdad.text = "¡Máximo 70 Años!"
                    txtErrorEdad.visibility = View.VISIBLE
                } else {
                    txtErrorEdad.visibility = View.GONE
                }
            } else {
                txtErrorEdad.visibility = View.GONE
            }
        }

        // Validación dinámica del ID
        val etxtId = findViewById<EditText>(R.id.etxtid)
        val txtErrorId = findViewById<TextView>(R.id.txtErrorId)

        etxtId.doOnTextChanged { text, start, before, count ->
            val idTexto = text.toString()

            if (idTexto.isNotEmpty()) {
                val id = idTexto.toLongOrNull()

                if (id == null) {
                    txtErrorId.text = "¡Sin Letras Ni Espacios!"
                    txtErrorId.visibility = View.VISIBLE
                } else {
                    txtErrorId.visibility = View.GONE
                }
            } else {
                txtErrorId.visibility = View.GONE
            }
        }

        // Botón para procesar e inscribir usuario
        val btnGuardar = findViewById<MaterialButton>(R.id.btnGuardarActivityInscribir)
        btnGuardar.setOnClickListener {
            ocultarTeclado()
            if (verificarDatosCompletos()) {
                if (verificarDatosCorrectos()) {
                    guardarDatos()
                    Toast.makeText(this, "¡Usuario Ha Sido Inscrito!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Botón para limpiar los datos del formulario
        val btnLimpiar = findViewById<MaterialButton>(R.id.btnLimpiarActivityInscribir)
        btnLimpiar.setOnClickListener {
            limpiar()
        }
    }

    /**
     * Revisa que ningún campo obligatorio se encuentre vacío.
     * @return true si todos los datos están completos; false si falta algún campo.
     */
    fun verificarDatosCompletos(): Boolean {
        val etxtNombre = findViewById<EditText>(R.id.etxtnombre)
        val etxtId = findViewById<EditText>(R.id.etxtid)
        val etxtEdad = findViewById<EditText>(R.id.etxtedad)
        val etxtMembresia = findViewById<AutoCompleteTextView>(R.id.etxtmembresia)

        val nombre: String = etxtNombre.text.toString().trim()
        val idTexto: String = etxtId.text.toString().trim()
        val edadTexto: String = etxtEdad.text.toString().trim()
        val membresia: String = etxtMembresia.text.toString().trim()

        val mapBool = mutableMapOf<String, Boolean>()
        mapBool["Nombre"] = nombre.isNotBlank()
        mapBool["ID o Cédula"] = idTexto.isNotBlank()
        mapBool["Edad"] = edadTexto.isNotBlank()
        mapBool["Membresia"] = membresia.isNotBlank()

        val falsos = mapBool.filterValues { !it }
        val contador = falsos.size

        when {
            contador > 1 -> Toast.makeText(this, "¡DATOS INCOMPLETOS!", Toast.LENGTH_SHORT).show()
            contador == 1 -> {
                val claveFaltante = falsos.keys.first()
                Toast.makeText(this, "¡Ingrese $claveFaltante!", Toast.LENGTH_SHORT).show()
            }
        }

        return mapBool.values.all { it }
    }

    /**
     * Valida que la estructura del ID sea convertible a Long y la Edad se encuentre en rango.
     * @return true si los campos son válidos; false si ocurre una excepción de formato.
     */
    fun verificarDatosCorrectos(): Boolean {
        val etxtId = findViewById<EditText>(R.id.etxtid)
        val etxtEdad = findViewById<EditText>(R.id.etxtedad)
        try {
            val idTexto: Long = etxtId.text.toString().toLong()
            try {
                val edadTexto: Int = etxtEdad.text.toString().toInt()
                if (edadTexto < 16 || edadTexto > 70) {
                    Toast.makeText(this, "¡Edad Incorrecta!", Toast.LENGTH_SHORT).show()
                    return false
                } else {
                    return true
                }
            } catch (e: Exception) {
                Toast.makeText(this, "¡Edad Incorrecta!", Toast.LENGTH_SHORT).show()
                return false
            }
        } catch (e: Exception) {
            Toast.makeText(this, "¡ID/Cédula Incorrecta!", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    /**
     * Serializa y añade el nuevo usuario ingresado dentro del archivo `miembros.json`.
     */
    fun guardarDatos() {
        val archivo = File(this.filesDir, "miembros.json")
        val jsonConfig = Json {
            prettyPrint = true
            encodeDefaults = true
        }
        val etxtNombre = findViewById<EditText>(R.id.etxtnombre)
        val etxtId = findViewById<EditText>(R.id.etxtid)
        val etxtEdad = findViewById<EditText>(R.id.etxtedad)
        val etxtMembresia = findViewById<AutoCompleteTextView>(R.id.etxtmembresia)

        val nombreTexto: String = etxtNombre.text.toString().trim()
        val idTexto: Long = etxtId.text.toString().toLong()
        val edadTexto: Int = etxtEdad.text.toString().toInt()
        val membresiaTexto: String = etxtMembresia.text.toString()
        val correoCalculado = "${nombreTexto.lowercase().replace(" ", "")}@gimnasio.com"

        val mapaLeido = if (archivo.exists() && archivo.readText().isNotBlank()) {
            jsonConfig.decodeFromString<MutableMap<Long, Usuario>>(archivo.readText())
        } else {
            mutableMapOf()
        }

        mapaLeido[idTexto] = Usuario(
            nombre = nombreTexto,
            id = idTexto,
            edad = edadTexto,
            membresia = membresiaTexto,
            correo = correoCalculado
        )

        archivo.writeText(jsonConfig.encodeToString(mapaLeido))
        limpiar()
    }

    /**
     * Limpia las entradas de texto del formulario.
     */
    fun limpiar() {
        val etxtNombre = findViewById<EditText>(R.id.etxtnombre)
        val etxtId = findViewById<EditText>(R.id.etxtid)
        val etxtEdad = findViewById<EditText>(R.id.etxtedad)
        val etxtMembresia = findViewById<AutoCompleteTextView>(R.id.etxtmembresia)
        etxtNombre.setText("")
        etxtId.setText("")
        etxtEdad.setText("")
        etxtMembresia.setText("")
    }
}