package project.eco.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import project.eco.ApiClient
import project.eco.navigation.Navigator
import project.eco.navigation.Screen


@Composable
fun RegisterScreen(navigator: Navigator) {
    var username by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    val apiClient = ApiClient()

    fun isValidUsername(username: String): Boolean {
        return username.isNotBlank() && !username.contains(" ")
    }

    fun isValidEmail(email: String): Boolean {
        // Basic email validation
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Registro de Usuario", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = lastname,
            onValueChange = { lastname = it },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Direccion") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                when {
                    !isValidUsername(username) -> errorMessage = "El usuario no puede contener espacios o estar vacio"
                    lastname.isEmpty() -> errorMessage = "Su apellido por favor"
                    !isValidEmail(email) -> errorMessage = "El email es invalido"
                    address.isEmpty() -> errorMessage = "La direccion no puede estar vacia"
                    password.isEmpty() -> errorMessage = "La contraseña no puede estar vacia"
                    confirmPassword.isEmpty() -> errorMessage = "Por favor confirme su contraseña"
                    password != confirmPassword -> errorMessage = "Las contraseñas no coinciden"
                    else -> {
                        errorMessage = ""
                        apiClient.registerUser(username, lastname, email, address, password) { response ->
                            // Manejar la respuesta de la API aquí
                            if (response.contains("User registered successfully")) {
                                Toast.makeText(context, usuarioRegistradoConExito(), Toast.LENGTH_SHORT).show()

                                // Navegar a la pantalla Login
                                navigator.navigateTo(Screen.LOGIN)
                            } else {
                                errorMessage = "No se ha podido registrar al usuario"
                            }
                        }
                    }
                }
            }
        ) {
            Text(text = "Register")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        Text(text = successMessage, color = MaterialTheme.colorScheme.primary)
    }
    // Botón que navega de vuelta a la pantalla principal cuando es clicado.
    Button(onClick = { navigator.navigateTo(Screen.MAIN) }) {
        Text(text = "Volver a Principal")
    }
}


fun usuarioRegistradoConExito(): String {
    return("¡Usuario registrado con exito!")
}