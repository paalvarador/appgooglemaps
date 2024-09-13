package com.paalvarador.appgooglemaps

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.paalvarador.appgooglemaps.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var latitud: EditText
    private lateinit var longitud: EditText
    private lateinit var searchButton: Button

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar los campos de texto y el boton
        latitud = findViewById(R.id.lat_input)
        longitud = findViewById(R.id.lng_input)
        searchButton = findViewById(R.id.search_button)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        searchButton.setOnClickListener {
            val lat = latitud.text.toString().toDoubleOrNull()
            val long = longitud.text.toString().toDoubleOrNull()

            if(lat != null && long != null){
                moveMapToLocation(lat, long)
            }else{
                Toast.makeText(this, "Por favor ingresar lat y long validos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        // val sydney = LatLng(-34.0, 151.0)
        // mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        // createMarker()

        // Habilitar la ubicación del usuario
        enableUserLocation()
    }

    /**
     * Funcion para crear un marcador de acuerdo a una latitud y longitud
     */
    private fun createMarker(){
        val favoritePlace = LatLng(-2.170998,-79.922359)
        mMap.addMarker(MarkerOptions().position(favoritePlace).title("Mi lugar favorito"))
        mMap.addMarker(MarkerOptions().position(favoritePlace).draggable(true))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(favoritePlace,18f),4000,null)
    }

    /**
     * Funcion para mover el mapa a un lugar de acuerdo a una latitud y una
     * longitud
     */
    private fun moveMapToLocation(latitude: Double, longitude: Double){
        val favoritePlace = LatLng(latitude,longitude)
        mMap.addMarker(MarkerOptions().position(favoritePlace).title("Nueva Ubicacion"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(favoritePlace,18f),4000,null)
    }

    /**
     * Funcion para habilitar la ubicación del usuario
     */
    private fun enableUserLocation(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // Pedir permisos de ubicación
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        mMap.isMyLocationEnabled = true

        // Obtener la ûltim ubicacion conocida del usuario
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if(location != null){
                var userLatLng = LatLng(location.latitude, location.longitude)

                // Mover la camara a la ultima ubicacion conocida del usuario
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng,18f),4000,null)

                // Anñadir un marcador
                mMap.addMarker(MarkerOptions().position(userLatLng).title("Mi ubicación"))

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableUserLocation()
                }else{
                    Snackbar.make(findViewById(R.id.map), "Permiso denegado", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
}