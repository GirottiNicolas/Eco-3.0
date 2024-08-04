package project.eco



import android.app.AlertDialog
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.infowindow.InfoWindow

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MapActivity : ComponentActivity() {



    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue = packageName
        val apiClient = ApiClient()
        setContent {

            val context = LocalContext.current
            val userLocation = UserLocation(context)

            val mapView = remember { MapView(this) }
            // Inicializar el LocationManager aquí
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            Column(
                modifier = Modifier
                    .background(Color.Transparent),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AndroidView(
                        factory = {
                            mapView.apply {
                                controller.setZoom(15.0)
                                setMultiTouchControls(true)
                                controller.setCenter(
                                    GeoPoint(
                                        -34.7653,
                                        -58.2120
                                    )
                                ) // Coordenadas de Berazategui, Argentina
                                zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // BOTON PARA VOLVER A LA PANTALLA PRINCIPAL
                Button(
                    onClick = {
                        finish()
                    },
                    modifier = Modifier
                ) {
                    Text(text = "Volver a la pantalla principal", fontSize = 16.sp)
                }

                // BOTON PARA ACTUALIZAR MAPA CON RECICLAJES
                Button(
                    onClick = {
                        updateMap(apiClient, mapView, userLocation)
                    },
                    modifier = Modifier
                ) {
                    Text(text = "Actualizar mapa con reciclajes", fontSize = 16.sp)
                }
            }
        }
    }

    private fun updateMap(apiClient: ApiClient, mapView: MapView, userLocation: UserLocation) {
        /*
            PROPOSITO: Actualizar el mapa dado a traves de la informacion obtenida
                        de reciclajes de la api dada.
        */
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val mapData = apiClient.getMapData()

                mapView.overlays.clear()
                mapData.forEach { data ->
                    data.coordenada_x?.let { x ->
                        data.coordenada_y?.let { y ->
                            val marker = Marker(mapView).apply {
                                position = GeoPoint(x, y)
                                title = data.direccion
                                icon = ContextCompat.getDrawable(this@MapActivity, R.drawable.marker)
                                setOnMarkerClickListener { m, _ ->
                                    m.showInfoWindow()
                                    true
                                }
                            }
                            marker.infoWindow = createInfoWindow(marker, mapView, userLocation)
                            mapView.overlays.add(marker)
                        }
                    }
                }
                mapView.invalidate()
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

    private fun createInfoWindow(marker: Marker, mapView: MapView, userLocation: UserLocation): InfoWindow {
        return object : InfoWindow(R.layout.bubble, mapView) {
            override fun onOpen(item: Any?) {
                val view = mView
                val button = view.findViewById<Button>(R.id.action_button)
                button.setOnClickListener {

                    navigateToRecycling(marker.position, mapView, userLocation)
                }
                val closeButton = view.findViewById<Button>(R.id.info_window_close_button)
                closeButton.setOnClickListener {
                    close()
                }
            }
            override fun onClose() {}
        }
    }


    // Navega hasta la ubicación de reciclaje
    private fun navigateToRecycling(destination: GeoPoint
                                    ,mapView: MapView
                                    ,userLocation: UserLocation) {
        CoroutineScope(Dispatchers.IO).launch {
            val currentLocation = userLocation.getCurrentLocation()

            try {
                if (currentLocation != null) {

                    // falla porque la distancia del userLocation es muy grande
                    val routePoints = getRoutePoints(currentLocation, destination)
                    CoroutineScope(Dispatchers.Main).launch {
                        addRouteToMap(routePoints, mapView)
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        showGpsRequiredDialog(userLocation.isGPSEnabled(locationManager), userLocation.hasLocationPermission(userLocation.userContext))
                        println("No se pudo obtener la ubicación actual: ${userLocation.hasLocationPermission(userLocation.userContext)}")
                    }

                }

            } catch (e: Exception) {
                Log.e("navigateToRecycling", "Error al obtener puntos de ruta: ${e.message}")
                Toast.makeText(this@MapActivity, "Error al calcular la ruta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showGpsRequiredDialog(gpsEnabled: Boolean, hasLocationPermission: Boolean) {
        val builder = AlertDialog.Builder(this)
        val (title, message) = when {
            !gpsEnabled -> "GPS no activado" to "El GPS debe estar activado para que se pueda trazar el camino."
            !hasLocationPermission -> "Permiso faltante" to "Se requiere el permiso de ubicación para que se pueda trazar el camino."
            else -> null to null
        }

        title?.let {
            builder.setTitle(it)
                .setMessage(message)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun getRoutePoints(start: GeoPoint, end: GeoPoint): List<GeoPoint> {
        val apiKey = "5b3ce3597851110001cf62485c13e3bc70b442b1a63dec0621ef1dff" // Reemplaza esto con tu clave API
        val urlString = "https://api.openrouteservice.org/v2/directions/foot-walking?api_key=$apiKey&start=${start.longitude},${start.latitude}&end=${end.longitude},${end.latitude}"

        return try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.readText()
            reader.close()
            // Parse the response JSON to extract route points
            // For simplicity, this example returns dummy data
            parseRoutePoints(response)
        } catch (e: Exception) {
            e.printStackTrace()
            listOf(start, end) // Fallback to dummy data
        }
    }

    private fun parseRoutePoints(response: String): List<GeoPoint> {
        val routePoints = mutableListOf<GeoPoint>()
        try {
            val jsonObject = JSONObject(response)
            val features = jsonObject.getJSONArray("features")
            if (features.length() > 0) {
                val geometry = features.getJSONObject(0).getJSONObject("geometry")
                val coordinates = geometry.getJSONArray("coordinates")

                for (i in 0 until coordinates.length()) {
                    val coord = coordinates.getJSONArray(i)
                    val lon = coord.getDouble(0)
                    val lat = coord.getDouble(1)
                    routePoints.add(GeoPoint(lat, lon))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return routePoints
    }



    private fun addRouteToMap(routePoints: List<GeoPoint>, mapView: MapView) {
        val polyline = Polyline().apply {
            setPoints(routePoints)

        }
        mapView.overlays.add(polyline)
        mapView.invalidate()
    }
}