package com.example.afekago.screens

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.afekago.models.RideRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DriverViewModel : ViewModel() {
    private val _rideRequests = MutableLiveData<List<RideRequest>>()
    val rideRequests: LiveData<List<RideRequest>> get() = _rideRequests

    fun fetchRideRequests() {
        val driverId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dbRef = FirebaseDatabase.getInstance().getReference("ride_requests")

        dbRef.get().addOnSuccessListener { snapshot ->
            val requests = snapshot.children.mapNotNull { it.getValue(RideRequest::class.java) }
                .filter { it.driverId == driverId && it.status == "pending" }
            _rideRequests.postValue(requests)
        }.addOnFailureListener {
            Log.e("DriverViewModel", "Error fetching ride requests", it)
        }
    }
}
