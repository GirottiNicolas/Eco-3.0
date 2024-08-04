package project.eco

import android.util.Log
import androidx.core.app.PendingIntentCompat.send
import io.ktor.client.*
import io.ktor.client.features.HttpRedirect
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocket
import io.ktor.http.*
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import project.eco.screens.RecyclingData



class ApiClient {
    /*
           El cliente Ktor está configurado para manejar JSON y habilitar el registro de niveles
           de cuerpo (LogLevel.BODY). Esto significa que las solicitudes y respuestas HTTP serán
           registradas en la consola, lo cual es útil para depuración pero puede deshabilitarse
           en producción.
    */
    @Serializable
    data class MapaReciclaje(
        val reciclaje_id: Int,
        val direccion: String,
        val coordenada_x: Double?,
        val coordenada_y: Double?,
        val recolector_id: Int?
    )

    @Serializable
    data class MapDataResponse(
        val map_data: List<MapaReciclaje>
    )

    private val client = HttpClient {
        // Configura el cliente Ktor para manejar JSON
        install(JsonFeature) {
            // Utiliza el serializador de Kotlinx para serializar y deserializar JSON
            serializer = KotlinxSerializer()
        }
        // Configura el cliente Ktor para registrar las solicitudes y respuestas HTTP
        install(Logging) {
            // Registra el nivel de cuerpo, lo cual incluye el contenido de las solicitudes y respuestas
            level = LogLevel.BODY
        }
        install(HttpRedirect) {
            followRedirects = false // No seguir redirecciones automáticamente
        }
    }
    suspend fun getMapData(): List<MapaReciclaje> {
        return try {
            val response: String = client.get("http://192.168.100.11:8000/open_map") {
                contentType(ContentType.Application.Json)
            }
            parseMapData(response)
        } catch (e: Exception) {
            println("Error fetching map data: ${e.message}")
            emptyList()
        }
    }

    private fun parseMapData(responseBody: String): List<MapaReciclaje> {
        return try {
            val json = Json { ignoreUnknownKeys = true }
            val response = json.decodeFromString<MapDataResponse>(responseBody)
            response.map_data
        } catch (e: Exception) {
            println("Error parsing map data: ${e.message}")
            emptyList()
        }
    }

    fun checkUserStatus(callback: (String) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response: HttpResponse = client.get("http://192.168.100.11:8000/status") {
                    contentType(ContentType.Application.Json)
                }
                val responseBody = response.readText()
                callback(responseBody)
            } catch (e: Exception) {
                callback("Error: ${e.message}")
            }
        }
    }


    fun logout(callback: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response: HttpResponse = client.post("http://192.168.100.11:8000/logout") {
                    contentType(ContentType.Application.Json)
                    // Envía un cuerpo vacío ya que no necesitamos datos adicionales
                    body = ""
                }
                val responseBody = response.readText()
                withContext(Dispatchers.Main) {
                    callback(responseBody)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback("Error: ${e.message}")
                }
            }
        }
    }

    fun registerUser(username: String, lastname: String, email: String, address: String, password: String, callback: (String) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response: HttpResponse = client.post("http://192.168.100.11:8000/register") {
                    contentType(ContentType.Application.Json)
                    body = mapOf(
                        "username" to username,
                        "lastname" to lastname,
                        "email" to email,
                        "address" to address,
                        "password" to password
                    )
                }
                val responseBody = response.readText()
                callback(responseBody)
            } catch (e: Exception) {
                callback("Error: ${e.message}")
            }
        }
    }

    fun checkCredentials(username: String, password: String, callback: (String) -> Unit) {
        /*
            El método checkCredentials realiza una solicitud POST con los parámetros
            de usuario y contraseña en formato JSON al endpoint especificado
            (http://yourserver.com/check).
        */
        // Lanza una corutina en el hilo principal

        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Realiza una solicitud POST a la URL especificada
                val response: HttpResponse = client.post("http://192.168.100.11:8000/login") {
                    // Establece el tipo de contenido como JSON
                    contentType(ContentType.Application.Json)
                    // Envía el cuerpo de la solicitud como un mapa con los parámetros de usuario y contraseña
                    body = mapOf("username" to username, "password" to password)
                }
                // Lee el texto de la respuesta
                val responseBody = response.readText()
                // Llama al callback con el cuerpo de la respuesta
                callback(responseBody)
            } catch (e: Exception) {
                // Si ocurre una excepción, llama al callback con el mensaje de error
                callback("Error hallado: ${e.message}")
            }
        }
    }

    fun sendRecyclingData(data: RecyclingData, callback: (String) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response: HttpResponse = client.post("http://192.168.100.11:8000/recycling") {
                    contentType(ContentType.Application.Json)
                    body = data
                }
                val responseBody = response.readText()
                callback(responseBody)
            } catch (e: Exception) {
                Log.e("RecyclingDataError", "Error sending recycling data: ${e.message}")
                callback("Error: ${e.message}")
            }
        }
    }


}


/*

Serializacion
    Serialización es el proceso de convertir un objeto en un formato que pueda ser
    fácilmente almacenado o transmitido. Este formato es típicamente una cadena de
    caracteres o una secuencia de bytes. En el contexto de JSON (JavaScript Object Notation),
    la serialización implica convertir un objeto en una cadena JSON.

Deserializacion
    Deserialización es el proceso inverso de la serialización. Consiste en convertir
    una cadena de texto o una secuencia de bytes (como una cadena JSON) de vuelta a un objeto.

Corutina
    Una corutina es una unidad de trabajo asincrónica que puede suspender su ejecución
    sin bloquear el hilo en el que se está ejecutando y luego reanudarla en un punto posterior.

    -No Bloqueantes: Las corutinas no bloquean el hilo en el que se están ejecutando.
    Esto significa que puedes iniciar una corutina en el hilo principal y realizar operaciones
    largas,como solicitudes de red o acceso a bases de datos, sin bloquear la interfaz de usuario.

    -Suspensión y Reanudación: Las corutinas pueden suspender su ejecución en puntos específicos
    (con la palabra clave suspend) y reanudarla más tarde, lo que permite realizar operaciones
    asincrónicas de manera más eficiente.

    Dispatcher: Un CoroutineDispatcher determina el hilo en el que se ejecuta una corutina.
    Kotlin proporciona varios dispatchers, como Dispatchers.Main para el hilo principal (UI thread),
    Dispatchers.IO para operaciones de entrada/salida, y Dispatchers.Default para trabajo intensivo
    en CPU.

*/