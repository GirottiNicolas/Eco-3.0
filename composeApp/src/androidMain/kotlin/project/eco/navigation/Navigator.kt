package project.eco.navigation


import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

// La enum class Screen define las diferentes pantallas de la aplicación.
enum class Screen {
    MAIN,
    RECYCLER_PROFILE,
    COLLECTOR_PROFILE,
    LOGIN,
    REGISTER
}

// La clase Navigator gestiona la navegación entre las diferentes pantallas de la aplicación.
class Navigator {
    // currentScreen almacena la pantalla actual y es observable para las composiciones.
    var currentScreen: Screen by mutableStateOf(Screen.MAIN)

    // navigateTo cambia la pantalla actual a la pantalla especificada.
    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }
}