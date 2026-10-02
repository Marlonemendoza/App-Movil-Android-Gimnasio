package com.example.gimnasio

import android.animation.Animator
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import kotlin.math.hypot

/**
 * Pantalla principal del menú de navegación.
 * Permite acceder a las distintas funcionalidades del gimnasio (Inscribir, Ver Inscritos,
 * Buscar/Editar/Eliminar y Filtrar correos de miembros Oro) utilizando animaciones personalizadas.
 */
class Activity2 : AppCompatActivity() {

    // Vista raíz para aplicar y controlar las animaciones de circular reveal (fadeIn/fadeOut)
    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Cargar el diseño XML correspondiente
        setContentView(R.layout.activity_2)

        // 2. Inicializar la vista raíz
        rootView = findViewById<View>(R.id.main)

        // 3. Ajustar márgenes para barras del sistema (edge-to-edge)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 4. Iniciar animación de revelado circular al cargarse la pantalla
        rootView.post {
            circularFadeIn(rootView)
        }

        // 5. Interceptar el evento de retroceso para reproducir la animación circular invertida
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                circularFadeOut(rootView)
            }
        })

        // Configuración de eventos clic para los botones de la interfaz
        val btnInscribir = findViewById<MaterialButton>(R.id.btninscribir)
        btnInscribir.setOnClickListener {
            val intent = Intent(this, InscribirActivity::class.java)
            aplicarTransicion(intent, R.anim.slide_in_right, R.anim.slide_out_left)
        }

        val btnInscritos = findViewById<MaterialButton>(R.id.btninscritos)
        btnInscritos.setOnClickListener {
            val intent = Intent(this, InscritosActivity::class.java)
            aplicarTransicion(intent, R.anim.slide_in_right, R.anim.slide_out_left)
        }

        val btnBuscar = findViewById<MaterialButton>(R.id.btnbuscar)
        btnBuscar.setOnClickListener {
            val intent = Intent(this, BuscarActivity::class.java)
            aplicarTransicion(intent, R.anim.slide_in_right, R.anim.slide_out_left)
        }

        val btnOro = findViewById<MaterialButton>(R.id.btnoro)
        btnOro.setOnClickListener {
            val intent = Intent(this, CorreosOroActivity::class.java)
            aplicarTransicion(intent, R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    /**
     * Realiza un efecto de animación de entrada circular (Circular Reveal) extendiéndose desde el centro.
     * @param view Vista sobre la que se aplicará el efecto.
     */
    fun circularFadeIn(view: View) {
        view.visibility = View.VISIBLE
        val cx = view.width / 2
        val cy = view.height / 2
        val finalRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()

        val circularAnim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius)
        circularAnim.duration = 600
        circularAnim.interpolator = AnimationUtils.loadInterpolator(view.context, android.R.anim.decelerate_interpolator)

        val fadeInAnim = AnimationUtils.loadAnimation(view.context, R.anim.fade_in)

        circularAnim.start()
        view.startAnimation(fadeInAnim)
    }

    /**
     * Aplica transiciones de deslizamiento al iniciar una nueva Activity de forma compatible con múltiples versiones de Android.
     * @param intent Intención de navegación.
     * @param animEntrada Recurso de animación de entrada.
     * @param animSalida Recurso de animación de salida.
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
     * Realiza un efecto de animación de salida circular (Circular Reveal) reduciéndose hacia el centro antes de finalizar la Activity.
     * @param view Vista sobre la que se aplicará el efecto.
     */
    fun circularFadeOut(view: View) {
        val cx = view.width / 2
        val cy = view.height / 2
        val startRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()

        val circularAnim = ViewAnimationUtils.createCircularReveal(view, cx, cy, startRadius, 0f)
        circularAnim.duration = 600
        circularAnim.interpolator = AnimationUtils.loadInterpolator(view.context, android.R.anim.decelerate_interpolator)

        circularAnim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator) {
                view.visibility = View.INVISIBLE
                finish() // Cierra la actividad actual
                @Suppress("DEPRECATION")
                overridePendingTransition(0, 0) // Previene parpadeos de transición nativa al regresar
            }
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        val fadeOutAnim = AnimationUtils.loadAnimation(view.context, R.anim.fade_out)
        circularAnim.start()
        view.startAnimation(fadeOutAnim)
    }
}