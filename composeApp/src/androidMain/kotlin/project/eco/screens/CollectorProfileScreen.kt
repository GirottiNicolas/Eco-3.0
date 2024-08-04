package project.eco.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import project.eco.MapActivity
import project.eco.R
import project.eco.UserLocation
import project.eco.navigation.Navigator
import project.eco.navigation.Screen

// CollectorProfileScreen es una función composable que muestra la pantalla del perfil del recolector.
@Composable
fun CollectorProfileScreen(navigator: Navigator) {

    // Obtiene el contexto actual para usarlo en los intentos de actividades.
    val context = LocalContext.current


    // Crea una columna que llena el tamaño completo de la pantalla, con padding y alineación.
    Column(
        modifier = Modifier
            .fillMaxSize() // La columna ocupa el ancho disponible.
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Mapa del reciclaje", fontSize = 24.sp)

        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(
                    data = R.drawable.mapa // Reemplaza con el nombre de tu imagen
                ).apply(block = fun ImageRequest.Builder.() {
                    crossfade(true)
                }).build()
            ),
            contentDescription = "Recycle Icon",
            modifier = Modifier.size(100.dp) // Ajusta el tamaño según sea necesario
        )

        // Agrega un espacio vertical de 16dp entre el texto y el siguiente elemento.
        Spacer(modifier = Modifier.height(16.dp))
        // Aquí puedes agregar más detalles del perfil del recolector

        // Botón que navega de vuelta a la pantalla principal cuando es clicado.
        Button(onClick = { navigator.navigateTo(Screen.MAIN) }) {
            Text(text = "Volver a Principal")
        }
        // Agrega un espacio vertical de 16dp entre el texto y el siguiente elemento.
        Spacer(modifier = Modifier.height(16.dp))
        // Botón para abrir el mapa
        Button(onClick = {

            // Crea un intent para iniciar project.eco.MapActivity.
            val intent = Intent(context, MapActivity::class.java)
            context.startActivity(intent) // Inicia la actividad de mapa.

        }) {
            Text(text = "Abrir Mapa")
        }
    }
}