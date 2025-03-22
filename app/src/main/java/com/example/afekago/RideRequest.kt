package com.example.afekago.models

data class RideRequest(
    val requestId: String = "",
    val driverId: String = "",
    val passengerId: String = "",
    val passengerName: String = "",
    val passengerPhone: String = "",
    val status: String = "pending",  // "pending", "accepted", "rejected"
    val requestType: String = "passenger_to_driver" // 🔹 נוסע לנהג או נהג לנוסע
)


