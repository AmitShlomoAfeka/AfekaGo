package com.example.afekago

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.afekago.adapters.TripsAdapter
import com.example.afekago.models.Trip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PlanTripActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var tripsList: ArrayList<Trip>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_trip)

        val passengerId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        recyclerView = findViewById(R.id.recyclerViewTrips)
        recyclerView.layoutManager = LinearLayoutManager(this)
        tripsList = arrayListOf()
        recyclerView.adapter = TripsAdapter(tripsList, passengerId)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }

        database = FirebaseDatabase.getInstance().getReference("plannedTrips")

        val btnAddTrip = findViewById<Button>(R.id.btnAddTrip)
        btnAddTrip.setOnClickListener {
            startActivity(Intent(this, AddTripActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        fetchTrips()
    }

    private fun fetchTrips() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tripsList.clear()
                if (!snapshot.exists()) {
                    addMockTrips() // ✅ שימוש בפונקציה במקרה שאין נסיעות כלל
                    return
                }

                for (tripSnapshot in snapshot.children) {
                    val trip = tripSnapshot.getValue(Trip::class.java)
                    trip?.let { tripsList.add(it) }
                }
                recyclerView.adapter?.notifyDataSetChanged()

                if (tripsList.isEmpty()) {
                    Toast.makeText(this@PlanTripActivity, "No planned trips available.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PlanTripActivity, "Error loading trips", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addMockTrips() {
        val mockTrips = listOf(
            Trip(driverId = "driver1", day = "Sunday", time = "09:00", type = "Driver"),
            Trip(driverId = "driver2", day = "Monday", time = "17:00", type = "Join"),
            Trip(driverId = "driver3", day = "Tuesday", time = "12:00", type = "Driver"),
            Trip(driverId = "driver4", day = "Wednesday", time = "15:00", type = "Join"),
            Trip(driverId = "driver5", day = "Thursday", time = "10:00", type = "Driver")
        )

        for (trip in mockTrips) {
            val tripId = database.push().key ?: continue
            database.child(tripId).setValue(trip)
        }

        Toast.makeText(this, "Mock trips added", Toast.LENGTH_SHORT).show()
        fetchTrips() // ✅ נטען מחדש את הנתונים לאחר ההוספה
    }
}
