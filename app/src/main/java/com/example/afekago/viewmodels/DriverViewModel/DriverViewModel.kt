package com.example.afekago.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.afekago.models.RideRequest
import com.google.firebase.database.*

class DriverViewModel : ViewModel() {
    private val _rideRequests = MutableLiveData<List<RideRequest>>()
    val rideRequests: LiveData<List<RideRequest>> get() = _rideRequests

    private val dbRef = FirebaseDatabase.getInstance().getReference("ride_requests")

    init {
        loadRideRequests()
    }

    private fun loadRideRequests() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val requests = snapshot.children.mapNotNull { it.getValue(RideRequest::class.java) }
                _rideRequests.value = requests
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
