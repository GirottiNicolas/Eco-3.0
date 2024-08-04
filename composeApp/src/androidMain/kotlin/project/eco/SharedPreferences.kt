package project.eco

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    // Inicializa SharedPreferences para almacenar datos de sesión en el archivo "user_prefs".
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val IS_LOGGED_IN = "is_logged_in"
        private const val USERNAME = "username"
    }

    fun saveLoginState(username: String) {
        // Guarda el estado de sesión del usuario, incluyendo si está logueado y su nombre de usuario.
        val editor = prefs.edit()

        // Establece el valor del estado de sesión (logueado) como true.
        editor.putBoolean(IS_LOGGED_IN, true)
        // Almacena el nombre de usuario en SharedPreferences.
        editor.putString(USERNAME, username)
        // Aplica los cambios realizados por el editor.
        // Los cambios se guardan en el archivo de SharedPreferences.
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGGED_IN, false)
    }

    fun getUsername(): String? {
        return prefs.getString(USERNAME, null)
    }

    private fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    fun logout() {
        clearSession()
    }
}