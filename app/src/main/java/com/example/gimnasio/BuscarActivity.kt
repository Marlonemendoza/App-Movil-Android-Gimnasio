package com.example.gimnasio

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
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
 * Pantalla que permite buscar un usuario por su ID o Cédula,
 * desplegar sus detalles y dar acceso a las acciones de Eliminación o Edición.
 */
class BuscarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_buscar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etxtId = findViewById<EditText>(R.id.etxtIdActivityBuscar)
        val txtErrorId = findViewById<TextView>(R.id.txtErrorId)
        val botonBuscar = findViewById<MaterialButton>(R.id.btnBuscarActivityBuscar)
        val txtMostrarUsuario = findViewById<TextView>(R.id.txtMostrarUsuarioActivityBuscar)

        val archivo = File(this.filesDir, "miembros.json")
        val jsonConfig = Json {
            prettyPrint = true
            encodeDefaults = true
        }

        // Validar si existen registros previos
        if (archivo.exists() && archivo.readText().isNotBlank()) {
            val contenidoJson = archivo.readText()
            val mapaLeido = jsonConfig.decodeFromString<MutableMap<Long, Usuario>>(contenidoJson)
            if (mapaLeido.size < 1) {
                txtMostrarUsuario.setText("¡Aún no hay usuarios inscritos!")
            } else {
                botonBuscar.visibility = View.VISIBLE
                txtMostrarUsuario.visibility = View.VISIBLE
                etxtId.visibility = View.VISIBLE
            }
        } else {
            txtMostrarUsuario.setText("¡Aún no hay usuarios inscritos!")
        }

        // Validación en tiempo real del campo ID
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

        // Acción al presionar el botón de búsqueda
        botonBuscar.setOnClickListener {
            ocultarTeclado()
            val txtMostrarUsuario = findViewById<TextView>(R.id.txtMostrarUsuarioActivityBuscar)
            txtMostrarUsuario.setText("")
            if (verificarDatosCorrectos()) {
                val etxtId = findViewById<EditText>(R.id.etxtIdActivityBuscar)
                val idTexto: Long = etxtId.text.toString().toLong()

                val usuario = Herramientas.buscarUsuarioEnJson(this, idTexto)
                txtMostrarUsuario.setText(usuario)

                if (usuario != "Usuario no encontrado" && usuario != "Aún no hay Usuarios Inscritos") {
                    val btnEliminar = findViewById<MaterialButton>(R.id.btnEliminarActivityBuscar)
                    val btnEditar = findViewById<MaterialButton>(R.id.btnEditarActivityBuscar)
                    btnEliminar.visibility = View.VISIBLE
                    btnEditar.visibility = View.VISIBLE

                    btnEliminar.setOnClickListener {
                        eliminarUsuario()
                    }
                    btnEditar.setOnClickListener {
                        editarUsuario(idTexto)
                    }
                }
            }
        }
    }

    /**
     * Remueve el usuario indicado del archivo JSON local y actualiza los controles de la UI.
     */
    fun eliminarUsuario() {
        val archivo = File(this.filesDir, "miembros.json")
        val jsonConfig = Json {
            prettyPrint = true
            encodeDefaults = true
        }
        val contenidoJson = archivo.readText()
        val mapaLeido = jsonConfig.decodeFromString<MutableMap<Long, Usuario>>(contenidoJson)
        val etxtId = findViewById<EditText>(R.id.etxtIdActivityBuscar)
        val id: Long = etxtId.text.toString().toLong()

        mapaLeido.remove(id)
        val jsonTexto = jsonConfig.encodeToString(mapaLeido)
        archivo.writeText(jsonTexto)

        val MostrarUsuario = findViewById<TextView>(R.id.txtMostrarUsuarioActivityBuscar)
        MostrarUsuario.setText("¡Usuario eliminado!")
        Toast.makeText(this, "¡Usuario Eliminado!", Toast.LENGTH_SHORT).show()
        etxtId.setText("")
    }

    /**
     * Inicia la actividad `EditarActivity` pasando el ID del usuario seleccionado como extra.
     * @param id Identificador único (Long) del usuario.
     */
    fun editarUsuario(id: Long) {
        val intent = Intent(this, EditarActivity::class.java)
        intent.putExtra("ID", id)
        aplicarTransicion(intent, R.anim.slide_in_right, R.anim.slide_out_left)
    }

    /**
     * Comprueba si la entrada del ID en el EditText se puede parsear correctamente como tipo Long.
     * @return true si el dato ingresado es válido; false en caso contrario.
     */
    fun verificarDatosCorrectos(): Boolean {
        val etxtId = findViewById<EditText>(R.id.etxtIdActivityBuscar)
        try {
            val idTexto: Long = etxtId.text.toString().toLong()
            Toast.makeText(this, "¡Búsqueda Realizada!", Toast.LENGTH_SHORT).show()
            return true
        } catch (e: Exception) {
            Toast.makeText(this, "¡ID/Cédula Mal Escrita!", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    /**
     * Aplica transiciones entre actividades según la versión del sistema operativo.
     */
    private fun aplicarTransicion(intent: Intent, animEntrada: Int, animSalida: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val options = ActivityOptions.makeCustomAnimation(this, animEntrada, animSalida)
            startActivity(intent, options.toBundle())
        } else {
            startActivity(intent)
            @Suppress("DEPRECATION")
            overridePendingTransition(animEntrada, animSalida)
        }
    }

    /**
     * Oculta el teclado suave desplegado en pantalla.
     */
    private fun ocultarTeclado() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}