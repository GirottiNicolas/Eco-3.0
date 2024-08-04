package project.eco.screens

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import okhttp3.Response
import project.eco.ApiClient
import project.eco.R
import project.eco.navigation.Navigator
import project.eco.navigation.Screen
import project.eco.SessionManager


@Composable
fun ImageFromResource() {
    Image(
        painter = painterResource(id = R.drawable.reciclar), // Usa R.drawable.<nombre_recurso> para el id
        contentDescription = "Descripción de la imagen",
        modifier = Modifier.size(100.dp) // Ajusta el tamaño según sea necesario
    )
}

@Composable
fun LoginScreen(navigator:Navigator, apiClient: ApiClient) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    // Check if the user is already logged in
    if (sessionManager.isLoggedIn()) {

        val loggedInUsername = sessionManager.getUsername()
        println("Usuario logueado: $loggedInUsername")
        // Redirect to the main screen
        navigator.navigateTo(Screen.MAIN)
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Agrega la imagen aquí usando Coil
        Image(
            painter = rememberAsyncImagePainter(ImageRequest.Builder(LocalContext.current).data(
                data = R.drawable.pequereciclar // Reemplaza con el nombre de tu imagen
            ).apply(block = fun ImageRequest.Builder.() {
                crossfade(true)
            }).build()
            ),
            contentDescription = "Recycle Icon",
            modifier = Modifier.size(100.dp) // Ajusta el tamaño según sea necesario
        )
        // Input usuario
        Text(text = "Proyecto Eco", fontSize = 30.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Iniciar sesión", fontSize = 22.sp)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // Al presionar el boton se verifican si los datos son correctos para su envio
            apiClient.checkCredentials(username, password) { response ->
                Log.d("LogInResponse", "Response from server: $response")
                if (response.contains("Valid credentials")) {
                    Toast.makeText(context, credencialesValidas(), Toast.LENGTH_SHORT).show()
                    sessionManager.saveLoginState(username)
                    // Navegar a la pantalla principal
                    navigator.navigateTo(Screen.MAIN)
                } else {
                    Toast.makeText(context, mensajeDeError(), Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text(text = "Iniciar Sesión")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { /* Acción para olvidar contraseña */ }) {
            Text(text = "Olvidé mi contraseña")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
    // Botón que navega de vuelta a la pantalla principal cuando es clicado.
    Button(onClick = { navigator.navigateTo(Screen.MAIN) }) {
        Text(text = "Volver a Principal")
    }
}


fun mensajeDeError(): String {
    return("Credenciales inválidas")
}

fun credencialesValidas(): String {
    return("¡Ha iniciado sesión de forma exitosa!")
}

