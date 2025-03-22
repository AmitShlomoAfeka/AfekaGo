package com.example.afekago.utils

import android.location.Location
import android.util.Log
import com.example.afekago.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.afekago.models.RideRequest
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject
import java.io.IOException
import com.google.android.gms.maps.model.LatLng
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response






fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
    val results = FloatArray(1)
    Location.distanceBetween(lat1, lon1, lat2, lon2, results)
    return results[0] / 1000  // המרחק בקילומטרים
}

fun findNearbyUsers(userType: String, radius: Int, callback: (List<User>) -> Unit) {
    val dbRef = FirebaseDatabase.getInstance().getReference("users")
    val currentUser = FirebaseAuth.getInstance().currentUser ?: return

    dbRef.child(currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val currentUserData = snapshot.getValue(User::class.java) ?: return
            val currentLat = currentUserData.latitude
            val currentLon = currentUserData.longitude

            dbRef.get().addOnSuccessListener { snapshot ->
                val usersList = mutableListOf<User>()
                snapshot.children.forEach { child ->
                    val user = child.getValue(User::class.java)
                    if (user != null && user.userType != currentUserData.userType) { // 🔹 נהג מחפש נוסעים, נוסע מחפש נהגים
                        val distance = calculateDistance(currentLat, currentLon, user.latitude, user.longitude)
                        if (distance <= radius) {
                            usersList.add(user)
                        }
                    }
                }
                callback(usersList)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            callback(emptyList())
        }
    })
}


fun sendRideRequest(driverId: String, passenger: User) {
    val passengerId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val requestId = FirebaseDatabase.getInstance().getReference("ride_requests").push().key ?: return

    val rideRequest = RideRequest(
        requestId = requestId,
        driverId = driverId,
        passengerId = passengerId,
        passengerName = passenger.name,
        passengerPhone = passenger.phoneNumber,
        status = "pending",
        requestType = "passenger_to_driver" // 🔹 בקשה של נוסע לנהג
    )

    FirebaseDatabase.getInstance().getReference("ride_requests").child(requestId).setValue(rideRequest)
}


fun updateRideRequestStatus(requestId: String, status: String) {
    FirebaseDatabase.getInstance().getReference("ride_requests").child(requestId).child("status").setValue(status)
}



fun getCurrentUserData(callback: (User?) -> Unit) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val dbRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

    dbRef.get().addOnSuccessListener { snapshot ->
        val user = snapshot.getValue(User::class.java)
        callback(user)
    }.addOnFailureListener {
        callback(null)
    }
}


fun getDirections(
    origin: LatLng,
    destination: LatLng,
    callback: (List<LatLng>) -> Unit
) {
    val apiKey = "AIzaSyCfTEkOtimN8KmjZZr0Z8xHREST_-do75c"
    val url = "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${destination.latitude},${destination.longitude}&key=$apiKey"

    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("RideFinder", "Failed to fetch directions", e)
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { responseData ->
                val routePoints = parseRoute(responseData)
                callback(routePoints)
            }
        }
    })
}

fun parseRoute(jsonData: String): List<LatLng> {
    val routePoints = mutableListOf<LatLng>()
    try {
        val jsonObject = JSONObject(jsonData)
        val routes = jsonObject.getJSONArray("routes")
        if (routes.length() > 0) {
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            val steps = legs.getJSONObject(0).getJSONArray("steps")

            for (i in 0 until steps.length()) {
                val step = steps.getJSONObject(i)
                val startLocation = step.getJSONObject("start_location")
                val endLocation = step.getJSONObject("end_location")


                routePoints.add(
                    LatLng(
                        startLocation.getDouble("lat"),
                        startLocation.getDouble("lng")
                    )
                )
                routePoints.add(
                    LatLng(
                        endLocation.getDouble("lat"),
                        endLocation.getDouble("lng")
                    )
                )
            }
        }
    } catch (e: Exception) {
        Log.e("RideFinder", "Error parsing route", e)
    }
    return routePoints
}


