package com.example.afekago.models

data class User(
    val id: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val userType: String = "",  // "Driver" או "Join"
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val radius: Int = 10,
    val passengers: Int = 1,
    val city: String = "",
    val notifications: Boolean = true
)
