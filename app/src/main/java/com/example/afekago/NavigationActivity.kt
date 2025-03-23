package com.example.afekago

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.example.afekago.utils.getDirections
import com.google.firebase.database.*

class NavigationActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var driverLocation: LatLng
    private lateinit var passengerLocation: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        val fallbackId = firebaseUser?.uid ?: run {
            Log.e("NavigationActivity", "No user is logged in")
            finish()
            return
        }

        val driverId = intent.getStringExtra("driverId") ?: fallbackId
        val passengerId = intent.getStringExtra("passengerId") ?: fallbackId

        getDriverLocation(driverId) { driverLoc ->
            driverLoc?.let {
                driverLocation = it
                checkAndDrawRoute()
            } ?: Log.e("NavigationActivity", "Failed to load driver location")
        }

        getPassengerLocation(passengerId) { passengerLoc ->
            passengerLoc?.let {
                passengerLocation = it
                checkAndDrawRoute()
            } ?: Log.e("NavigationActivity", "Failed to load passenger location")
        }

        val finishRideButton = findViewById<Button>(R.id.btnFinishRide)
        finishRideButton.setOnClickListener {
            val intent = Intent(this, PaymentMethodActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private fun checkAndDrawRoute() {
        if (::driverLocation.isInitialized && ::passengerLocation.isInitialized) {
            drawRoute()
        }
    }

    private fun drawRoute() {
        getDirections(driverLocation, passengerLocation) { route ->
            val polylineOptions = PolylineOptions()
                .addAll(route)
                .width(10f)
                .color(Color.BLUE)
                .geodesic(true)

            // פתרון לקריסת ה־UI
            runOnUiThread {
                mMap.addPolyline(polylineOptions)

                mMap.addMarker(MarkerOptions().position(driverLocation).title("Driver"))
                mMap.addMarker(MarkerOptions().position(passengerLocation).title("Passenger"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(passengerLocation, 12f))
            }
        }
    }



    private fun getDriverLocation(driverId: String, callback: (LatLng?) -> Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(driverId)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lat = snapshot.child("latitude").getValue(Double::class.java)
                val lng = snapshot.child("longitude").getValue(Double::class.java)
                callback(if (lat != null && lng != null) LatLng(lat, lng) else null)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    
    private fun getPassengerLocation(passengerId: String, callback: (LatLng?) -> Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(passengerId)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lat = snapshot.child("latitude").getValue(Double::class.java)
                val lng = snapshot.child("longitude").getValue(Double::class.java)
                callback(if (lat != null && lng != null) LatLng(lat, lng) else null)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }
}
