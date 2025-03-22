package com.example.afekago.utils

import com.example.afekago.models.User
import com.google.firebase.database.FirebaseDatabase

fun populateTestUsers() {
    val dbRef = FirebaseDatabase.getInstance().getReference("users")

    val users = listOf(
        User("user1", "Pnina Levi", "052-1234567", "driver1@afeka.ac.il", "Driver", 32.0853, 34.7818, 15, 2, "Tel Aviv", true),
        User("user2", "Dana Cohen", "054-9876543", "pass1@afeka.ac.il", "Join", 32.0854, 34.7820, 10, 1, "Tel Aviv", true),
        User("user3", "Itay Bar", "050-3334445", "driver2@afeka.ac.il", "Driver", 32.0900, 34.7850, 20, 3, "Herzliya", true),
        User("user4", "Maya Levy", "053-9998877", "pass2@afeka.ac.il", "Join", 32.0880, 34.7800, 8, 1, "Tel Aviv", false)
    )

    users.forEach { user ->
        dbRef.child(user.id).setValue(user)
    }
}
