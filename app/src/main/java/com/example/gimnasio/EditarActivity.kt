package com.example.gimnasio

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Pantalla para modificar la información de un usuario registrado.
 * Recibe el ID del usuario a modificar mediante Extras de Intent.
 */
class EditarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el ID enviado desde BuscarActivity
        val idRecibido = intent.getLongExtra("ID", -1L)

        val jsonConfig = Json {
            prettyPrint = true
            encodeDefaults = true
        }
        val archivo = File(this.filesDir, "miembros.json")

        // Cargar y autocompletar los campos con la información actual del usuario
        if (archivo.exists() && archivo.readText().isNotBlank()) {
            try {
                val contenidoJson = archivo.readText()
                val mapaLeido = jsonConfig.decodeFromString<MutableMap<Long, Usuario>>(contenidoJson)

                val usuario = mapaLeido[idRecibido]
                if (usuario != null) {
                    val nombre = findViewById<EditText>(R.id.etxtnombreActivityEditar)
                    val cedula = findViewById<EditText>(R.id.etxtidActivityEditar)
                    val edad = findViewById<EditText>(R.id.etxtedadActivityEditar)
                    val etxtMembresia = findViewById<AutoCompleteTextView>(R.id.etxtmembresiaActivityEditar)

                    nombre.setText(usuario.nombre)
                    cedula.setText(usuario.id.toString())
                    edad.setText(usuario.edad.toString())
                    etxtMembresia.setText(usuario.membresia, false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error al leer la base de datos de miembros", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No hay datos para editar", Toast.LENGTH_SHORT).show()
        }

        // Configuración del dropdown para tipo de membresía
        val opcionesMembresia = arrayOf("Bronce", "Plata", "Oro")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, opcionesMembresia)
        val dropdown = findViewById<AutoCompleteTextView>(R.id.etxtmembresiaActivityEditar)
        dropdown.setAdapter(adapter)

        dropdown.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                ocultarTeclado()
                dropdown.showDropDown()
            }
        }

        // Validación dinámica de límites y tipo numérico en Edad
        val etxtEdad = findViewById<EditText>(R.id.etxtedadActivityEditar)
        val txtErrorEdad = findViewById<TextView>(R.id.txtErrorEdadActivityEditar)

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

        // Validación dinámica del campo ID
        val etxtId = findViewById<EditText>(R.id.etxtidActivityEditar)
        val txtErrorId = findViewById<TextView>(R.id.txtErrorIdActivityEditar)

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

        // Evento guardar datos
        val btnGuardar = findViewById<MaterialButton>(R.id.btnGuardarActivityEditar)
        btnGuardar.setOnClickListener {
            ocultarTeclado()
            if (verificarDatosCompletos()) {
                if (verificarDatosCorrectos()) {
                    guardarDatos(idRecibido)
                    limpiar()
                    Toast.makeText(this, "¡Usuario Editado!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Verifica que todos los campos requeridos en el formulario no estén vacíos.
     * @return true si todos los campos están completos, false de lo contrario.
     */
    fun verificarDatosCompletos(): Boolean {
        val etxtNombre = findViewById<EditText>(R.id.etxtnombreActivityEditar)
        val etxtId = findViewById<EditText>(R.id.etxtidActivityEditar)
        val etxtEdad = findViewById<EditText>(R.id.etxtedadActivityEditar)
        val etxtMembresia = findViewById<AutoCompleteTextView>(R.id.etxtmembresiaActivityEditar)

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
     * Valida que los valores ingresados (como Edad e ID) cumplan con los rangos numéricos requeridos.
     * @return true si los valores son válidos, false en caso contrario.
     */
    fun verificarDatosCorrectos(): Boolean {
        val etxtId = findViewById<EditText>(R.id.etxtidActivityEditar)
        val etxtEdad = findViewById<EditText>(R.id.etxtedadActivityEditar)
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
     * Guarda la nueva información ingresada en el archivo `miembros.json`.
     * Si el ID cambió, elimina la clave antigua y registra la nueva entrada.
     * @param idParametro ID original antes de la modificación.
     */
    fun guardarDatos(idParametro: Long) {
        val archivo = File(this.filesDir, "miembros.json")
        val jsonConfig = Json {
            prettyPrint = true
            encodeDefaults = true
        }
        val etxtNombre = findViewById<EditText>(R.id.etxtnombreActivityEditar)
        val etxtId = findViewById<EditText>(R.id.etxtidActivityEditar)
        val etxtEdad = findViewById<EditText>(R.id.etxtedadActivityEditar)
        val etxtMembresia = findViewById<AutoCompleteTextView>(R.id.etxtmembresiaActivityEditar)

        val nombreTexto: String = etxtNombre.text.toString().trim()
        val idTexto: Long = etxtId.text.toString().toLong()
        val edadTexto: Int = etxtEdad.text.toString().toInt()
        val membresiaTexto: String = etxtMembresia.text.toString()

        val mapaLeido = if (archivo.exists() && archivo.readText().isNotBlank()) {
            try {
                jsonConfig.decodeFromString<MutableMap<Long, Usuario>>(archivo.readText())
            } catch (e: Exception) {
                mutableMapOf()
            }
        } else {
            mutableMapOf()
        }

        // Si se modificó la cédula o ID, remover el registro asociado al ID viejo
        if (idParametro != idTexto) {
            mapaLeido.remove(idParametro)
        }

        mapaLeido[idTexto] = Usuario(
            nombre = nombreTexto,
            id = idTexto,
            edad = edadTexto,
            membresia = membresiaTexto,
            correo = "${nombreTexto.lowercase().replace(" ", "")}@gimnasio.com"
        )
        archivo.writeText(jsonConfig.encodeToString(mapaLeido))
    }

    /**
     * Resetea el contenido numérico y de texto de los campos de edición.
     */
    fun limpiar() {
        val etxtNombre = findViewById<EditText>(R.id.etxtnombreActivityEditar)
        val etxtId = findViewById<EditText>(R.id.etxtidActivityEditar)
        val etxtEdad = findViewById<EditText>(R.id.etxtedadActivityEditar)
        val etxtMembresia = findViewById<AutoCompleteTextView>(R.id.etxtmembresiaActivityEditar)
        etxtNombre.setText("")
        etxtId.setText("")
        etxtEdad.setText("")
        etxtMembresia.setText("")
    }

    /**
     * Oculta el teclado suave.
     */
    private fun ocultarTeclado() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}