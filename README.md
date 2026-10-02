# 🏋️ App Móvil Android -Gimnasio

Aplicación móvil nativa para la administración y control de miembros de un gimnasio, desarrollada en **Kotlin** para Android. Ofrece un flujo CRUD completo (Crear, Leer, Actualizar, Eliminar) con persistencia de datos local en **JSON**, validaciones dinámicas de entrada y animaciones avanzadas de interfaz (*Circular Reveal*, transiciones de deslizamiento y *SplashScreen API*).

---

## 🚀 Características y Funcionalidades

- **Pantalla de Bienvenida (SplashScreen API):**
  - Pantalla inicial con integración nativa de `androidx.core.splashscreen.SplashScreen`.
  - Transición suave hacia el menú principal.

- **Menú Interactivo con Animación Circular (*Circular Reveal*):**
  - Animación de entrada y salida `ViewAnimationUtils.createCircularReveal` combinada con `fade_in` y `fade_out`.
  - Manejo del botón de retroceso (`OnBackPressedDispatcher`) para ejecutar la animación inversa de cierre.

- **Inscripción y Registro de Miembros (`InscribirActivity`):**
  - Registro de usuarios con campos: **Nombre**, **Cédula / ID** (`Long`), **Edad** e **Ingreso de Membresía** (`AutoCompleteTextView` con menú desplegable Material Design).
  - Validaciones dinámicas en tiempo real con `doOnTextChanged`:
    - Rango de edad permitido: **16 a 70 años**.
    - Validación numérica de Cédula/ID (sin letras ni espacios).
  - Generación automática de correo corporativo ficticio (ej. `nombreusuario@gimnasio.com`).

- **Consulta General de Inscritos (`InscritosActivity`):**
  - Lectura del archivo de almacenamiento y desplegado ordenado de todos los miembros registrados.

- **Búsqueda, Edición y Bajas de Usuarios (`BuscarActivity` & `EditarActivity`):**
  - Búsqueda directa por número de documento / ID.
  - Edición completa de datos almacenados con actualización de claves en caso de cambio de documento.
  - Eliminación segura de registros seleccionados del archivo local.

- **Filtrado de Clientes Preferenciales (`CorreosOroActivity`):**
  - Filtro exclusivo que extrae y muestra las direcciones de correo de los afiliados con membresía **"Oro"**.

---

## 🛠️ Tecnologías y Librerías Utilizadas

- **Lenguaje:** [Kotlin](https://kotlinlang.org/)
- **Entorno de Desarrollo:** [Android Studio](https://developer.android.com/studio)
- **Persistencia de Datos:** `kotlinx.serialization` (JSON local en el almacenamiento interno de la app via `filesDir`).
- **Librerías UI:** Material Components (`MaterialButton`, `TextInputLayout`), `ConstraintLayout`.
- **SplashScreen API:** `androidx.core:core-splashscreen`.
- **Animaciones Personales (`res/anim` & `res/drawable`):**
  - Transiciones personalizadas de entrada/salida (`slide_in_right`, `slide_out_left`, `fade_in`, `fade_out`).
  - Botones con selectores de estado 3D y bordes redondeados (`boton_inscribir3d.xml`, `curvar_esquinas.xml`).

---

## 📂 Estructura del Proyecto

```text
com.example.gimnasio/
├── MainActivity.kt         # Pantalla Splash inicial y bienvenida
├── Activity2.kt            # Menú principal con animación Circular Reveal
├── InscribirActivity.kt     # Formulario de registro y validaciones
├── InscritosActivity.kt    # Vista general de miembros
├── BuscarActivity.kt       # Búsqueda, consulta y opción de baja/edición
├── EditarActivity.kt       # Modificación de datos de miembros
├── CorreosOroActivity.kt   # Filtrado de miembros de plan Oro
├── Herramientas.kt         # Objeto utilitario (Búsqueda JSON y Ocultar teclado)
└── Usuario.kt              # Data class serializable (@Serializable)

res/
├── anim/                   # Animaciones de transición (slide y fade)
├── drawable/               # Fondos, selectores 3D y formas vectoriales
└── layout/                 # Vistas XML de cada Activity
