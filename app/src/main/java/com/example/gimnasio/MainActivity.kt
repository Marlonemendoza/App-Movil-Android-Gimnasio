package com.example.gimnasio

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

/**
 * Pantalla inicial de bienvenida. Muestra una pantalla de carga (SplashScreen API)
 * antes de dar paso a la navegación principal a través de `Activity2`.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Inicializar la SplashScreen API de Android Core
        val splashScreen = installSplashScreen()

        // Control para definir la permanencia de la pantalla de bienvenida
        var mantenerEnPantalla = true
        splashScreen.setKeepOnScreenCondition { mantenerEnPantalla }

        // 2. Temporizador para ocultar el Splash Screen tras 1 segundo
        Handler(Looper.getMainLooper()).postDelayed({
            mantenerEnPantalla = false
        }, 1000)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Botón para continuar hacia el menú de opciones (Activity2)
        val btnContinuar = findViewById<MaterialButton>(R.id.btncontinuar)
        btnContinuar.setOnClickListener {
            val intent = Intent(this, Activity2::class.java)
            startActivity(intent)

            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }
}