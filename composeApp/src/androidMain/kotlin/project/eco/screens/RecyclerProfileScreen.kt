package project.eco.screens


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlinx.serialization.Serializable
import project.eco.navigation.Navigator
import project.eco.navigation.Screen
import project.eco.ApiClient
import project.eco.R
import project.eco.SessionManager
import java.util.Locale

@Serializable
data class RecyclingData(
    val vidrio: Boolean,
    val metal: Boolean,
    val plastico: Boolean,
    val carton: Boolean,
    val usuarioDeReciclaje: String?
)


@Composable
fun RecyclerProfileScreen(navigator: Navigator) {
    val apiClient = ApiClient()
    val context = LocalContext.current
    val vidrio = remember { mutableStateOf(false) }
    val metal = remember { mutableStateOf(false) }
    val plastico = remember { mutableStateOf(false) }
    val carton = remember { mutableStateOf(false) }
    val todos = remember { mutableStateOf(false) }
    val sessionManager = SessionManager(context)
    val username = sessionManager.getUsername()
        ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

    fun updateAllCheckboxes(value: Boolean) {
        vidrio.value = value
        metal.value = value
        plastico.value = value
        carton.value = value
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Row para el nombre de usuario
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            username?.let {
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.Gray)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Reciclador: $it",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp), // Padding superior para dejar espacio para el nombre de usuario
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Agrega la imagen aquí usando Coil
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current).data(
                        data = R.drawable.reciclajeclasificado // Reemplaza con el nombre de tu imagen
                    ).apply(block = fun ImageRequest.Builder.() {
                        crossfade(true)
                    }).build()
                ),
                contentDescription = "Recycle Icon",
                modifier = Modifier.size(175.dp) // Ajusta el tamaño según sea necesario
            )

            Text(
                text = "Selecciona los tipos de residuos:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = vidrio.value, onCheckedChange = { vidrio.value = it })
                Text(text = "Vidrio  ")
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(
                            data = R.drawable.glass // Reemplaza con el nombre de tu imagen
                        ).apply(block = fun ImageRequest.Builder.() {
                            crossfade(true)
                        }).build()
                    ),
                    contentDescription = "Vidrio",
                    modifier = Modifier.size(30.dp)
                )

            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = metal.value, onCheckedChange = { metal.value = it })
                Text(text = "Metal  ")
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(
                            data = R.drawable.metal // Reemplaza con el nombre de tu imagen
                        ).apply(block = fun ImageRequest.Builder.() {
                            crossfade(true)
                        }).build()
                    ),
                    contentDescription = "Metal",
                    modifier = Modifier.size(30.dp)
                )

            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = plastico.value, onCheckedChange = { plastico.value = it })
                Text(text = "Plástico  ")
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(
                            data = R.drawable.plastic // Reemplaza con el nombre de tu imagen
                        ).apply(block = fun ImageRequest.Builder.() {
                            crossfade(true)
                        }).build()
                    ),
                    contentDescription = "Plástico",
                    modifier = Modifier.size(30.dp)
                )

            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = carton.value, onCheckedChange = { carton.value = it })
                Text(text = "Cartón  ")
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(
                            data = R.drawable.box // Reemplaza con el nombre de tu imagen
                        ).apply(block = fun ImageRequest.Builder.() {
                            crossfade(true)
                        }).build()
                    ),
                    contentDescription = "Cartón",
                    modifier = Modifier.size(30.dp)
                )

            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = todos.value, onCheckedChange = {
                    todos.value = it
                    updateAllCheckboxes(it)
                })
                Text(text = "Todos los tipos de reciclaje")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                if (!vidrio.value && !metal.value && !plastico.value && !carton.value) {
                    Toast.makeText(context, "Selecciona al menos un tipo de residuo", Toast.LENGTH_SHORT).show()
                } else {
                    val data = RecyclingData(
                        vidrio = vidrio.value,
                        metal = metal.value,
                        plastico = plastico.value,
                        carton = carton.value,
                        usuarioDeReciclaje = sessionManager.getUsername()
                    )
                    apiClient.sendRecyclingData(data) { response ->
                        if (response.contains("success")) {
                            Toast.makeText(context, "Datos enviados exitosamente", Toast.LENGTH_SHORT).show()
                            navigator.navigateTo(Screen.MAIN)
                        } else {
                            Toast.makeText(context, "Error al enviar datos", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }) {
                Text(text = "Enviar")
            }
            // Botón que navega de vuelta a la pantalla principal cuando es clicado.
            Button(onClick = { navigator.navigateTo(Screen.MAIN) }) {
                Text(text = "Volver a Principal")
            }
        }
    }
}