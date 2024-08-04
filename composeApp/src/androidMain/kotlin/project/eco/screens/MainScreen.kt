
package project.eco.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import okhttp3.Response
import project.eco.ApiClient
import project.eco.R
import project.eco.navigation.Navigator
import project.eco.navigation.Screen
import project.eco.SessionManager




@Composable
fun MainScreen(navigator: Navigator) {
    /*
        PROPOSITO: Pantalla principal. Muestra los botones que corresponden a un usuario
                    y su estado de sesion.
    */
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val apiClient = ApiClient()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "Proyecto Eco", fontSize = 30.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(
                data = R.drawable.pequereciclar // Reemplaza con el nombre de tu imagen
            ).apply(block = fun ImageRequest.Builder.() {
                crossfade(true)
            }).build()
            ),
            contentDescription = "Recycle Icon",
            modifier = Modifier.size(100.dp) // Ajusta el tamaño según sea necesario
        )

        Spacer(modifier = Modifier.height(20.dp))




        // Mostrar el botón de inicio de sesión solo si el usuario no está loggeado
        if (!sessionManager.isLoggedIn()) {
            Button(onClick = { navigator.navigateTo(Screen.LOGIN) }) {
                Text(text = "Iniciar sesión", fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Mostrar el botón de registro solo si el usuario no está loggeado
            Button(onClick = { navigator.navigateTo(Screen.REGISTER) }) {
                Text(text = "Registrarse", fontSize = 22.sp)
            }
        } else {
            // Mostrar el botón de perfil de recicladores
            Button(onClick = { navigator.navigateTo(Screen.RECYCLER_PROFILE) }) {
                Text(text = "Recicladores")
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar el botón de perfil de recolectores
            Button(onClick = { navigator.navigateTo(Screen.COLLECTOR_PROFILE) }) {
                Text(text = "Recolectores")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    cerrarSesionDeUsuario(apiClient,sessionManager,navigator,context)
                },
                modifier = Modifier
                    .padding(4.dp) // Ajusta el padding si es necesario
            ) {
                Text(text = "Cerrar Sesión")
            }
        }
    }
}



fun cerrarSesionDeUsuario(apiClient: ApiClient, sessionManager: SessionManager, navigator: Navigator, context: Context){
    apiClient.logout { response ->
        Log.d("LogoutResponse", "Response from server: $response")
        if (response.contains("Logged out successfully")) {
            sessionManager.logout()
            Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            navigator.navigateTo(Screen.LOGIN)
        } else {
            Log.e("LogoutError", "Error response: $response")
            Toast.makeText(context, "Error al cerrar sesión: $response", Toast.LENGTH_LONG).show()
        }
    }}