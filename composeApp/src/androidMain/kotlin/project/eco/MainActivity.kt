package project.eco

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.jetbrains.compose.resources.painterResource
import project.eco.navigation.Navigator
import project.eco.navigation.Screen
import project.eco.screens.CollectorProfileScreen
import project.eco.screens.RecyclerProfileScreen
import project.eco.screens.MainScreen
import project.eco.screens.RegisterScreen
import project.eco.screens.LoginScreen



// MainActivity es la actividad principal que se ejecuta al iniciar la aplicación.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Crea y recuerda una instancia de Navigator para gestionar
            // la navegación entre pantallas.
            val navigator = remember { Navigator() }
            // Llama a la función AppContent pasándole el navigator como parámetro.
            AppContent(navigator)
            // Agregar imagen

        }
    }
}

// AppContent es una función composable que muestra el contenido
// de la aplicación basado en la pantalla actual.
@Composable
fun AppContent(navigator: Navigator) {
    when (navigator.currentScreen) {
        // Si la pantalla actual es MAIN, se muestra MainScreen. Asi con cada una
        Screen.MAIN -> MainScreen(navigator)
        Screen.RECYCLER_PROFILE -> RecyclerProfileScreen(navigator)
        Screen.COLLECTOR_PROFILE -> CollectorProfileScreen(navigator)
        Screen.LOGIN -> LoginScreen(navigator, apiClient = ApiClient())
        Screen.REGISTER -> RegisterScreen(navigator)
    }
}