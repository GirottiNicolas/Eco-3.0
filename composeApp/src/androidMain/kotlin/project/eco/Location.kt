package project.eco

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import org.osmdroid.util.GeoPoint



class UserLocation(private val context: Context) {
    var userContext: Context
        get() = context
        // No se necesita setter porque el context no debería cambiar después de la inicialización
        private set(value) {
            throw UnsupportedOperationException("Context no puede ser modificado")
        }

    fun hasLocationPermission(context: Context): Boolean {
        // PROPOSITO: Indica si el usuario tiene los permisos de ubicacion.
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isGPSEnabled(locationManager: LocationManager): Boolean {
        // PROPOSITO: Indica si el gps esta encendido.
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
    }

    @SuppressLint("MissingPermission")
    private fun bestLocationOfUser(locationManager: LocationManager): GeoPoint?{
        val providers = locationManager.getProviders(true)
        var bestLocation: Location? = null

        for (provider in providers) {
            val location = locationManager.getLastKnownLocation(provider) ?: continue

            if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                bestLocation = location
            }
        }
        // Convierte la mejor ubicación encontrada en un objeto GeoPoint y lo retorna
        return bestLocation?.let { GeoPoint(it.latitude, it.longitude) }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(): GeoPoint? {
        /*
            PROPOSITO: Describe la ubicacion actual del usuario si tiene el permiso y
                        el gps encendido, caso contrario describe null.
        */
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Verificar permisos
        return if (hasLocationPermission(context) and isGPSEnabled(locationManager)) {
            // Tiene los permisos, entonces envia la ubicacion
            (bestLocationOfUser(locationManager))
        }else {
            // No tiene los permisos
            null
        }
    }

}

// Crear una ventana si el gps no esta activado, diciendole al usuario que lo prenda para que
// funcione correctamente el mapa.



