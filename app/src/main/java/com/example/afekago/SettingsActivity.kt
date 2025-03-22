package com.example.afekago

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SettingsActivity : AppCompatActivity() {
    private lateinit var tvUserName: TextView
    private lateinit var tvPhoneNumber: TextView
    private lateinit var tvRadiusLabel: TextView
    private lateinit var tvPassengersLabel: TextView
    private lateinit var seekRadius: SeekBar
    private lateinit var seekPassengers: SeekBar
    private lateinit var switchNotifications: SwitchCompat
    private lateinit var spinnerCity: Spinner
    private lateinit var btnBack: ImageButton
    private lateinit var userTypeGroup: RadioGroup
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val databaseReference = FirebaseDatabase.getInstance().getReference("users")
    private val auth = FirebaseAuth.getInstance()
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        btnBack = findViewById(R.id.btnBack)
        tvUserName = findViewById(R.id.tvUserName)
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber)
        tvRadiusLabel = findViewById(R.id.tvRadiusLabel)
        tvPassengersLabel = findViewById(R.id.tvPassengersLabel)
        seekRadius = findViewById(R.id.seekBarRadius)
        seekPassengers = findViewById(R.id.seekBarPassengers)
        switchNotifications = findViewById(R.id.switchNotifications)
        spinnerCity = findViewById(R.id.spinnerCity)
        userTypeGroup = findViewById(R.id.userTypeGroup)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        btnBack.setOnClickListener { finish() }

        checkLocationPermission()
        loadUserData()
        setupListeners()
    }

    private fun checkLocationPermission() {
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fine != PackageManager.PERMISSION_GRANTED || coarse != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser ?: return
        val userId = user.uid

        tvPhoneNumber.text = user.phoneNumber ?: "Unknown"
        tvUserName.text = user.displayName ?: "Guest"

        databaseReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.child("name").getValue(String::class.java)?.let {
                    tvUserName.text = it
                }
                snapshot.child("radius").getValue(Int::class.java)?.let {
                    seekRadius.progress = it
                    tvRadiusLabel.text = "Radius: $it km"
                }
                snapshot.child("passengers").getValue(Int::class.java)?.let {
                    seekPassengers.progress = it
                    tvPassengersLabel.text = "Passengers: $it"
                }
                snapshot.child("notifications").getValue(Boolean::class.java)?.let {
                    switchNotifications.isChecked = it
                }
                snapshot.child("city").getValue(String::class.java)?.let { city ->
                    val pos = (spinnerCity.adapter as ArrayAdapter<String>).getPosition(city)
                    spinnerCity.setSelection(pos)
                }
                snapshot.child("userType").getValue(String::class.java)?.let {
                    if (it == "Driver") userTypeGroup.check(R.id.rbDriver) else userTypeGroup.check(R.id.rbJoin)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SettingsActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupListeners() {
        seekRadius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvRadiusLabel.text = "Radius: $progress km"
                saveUserSettings()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekPassengers.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvPassengersLabel.text = "Passengers: $progress"
                saveUserSettings()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        switchNotifications.setOnCheckedChangeListener { _, _ -> saveUserSettings() }

        spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                saveUserSettings()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        userTypeGroup.setOnCheckedChangeListener { _, _ -> saveUserSettings() }
    }

    private fun saveUserSettings() {
        val user = auth.currentUser ?: return
        val dbRef = databaseReference.child(user.uid)

        val selectedType = when (userTypeGroup.checkedRadioButtonId) {
            R.id.rbDriver -> "Driver"
            R.id.rbJoin -> "Join"
            else -> "Join"
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            val updates = mapOf(
                "radius" to seekRadius.progress,
                "passengers" to seekPassengers.progress,
                "city" to spinnerCity.selectedItem.toString(),
                "notifications" to switchNotifications.isChecked,
                "userType" to selectedType,
                "latitude" to (location?.latitude ?: 32.0853),
                "longitude" to (location?.longitude ?: 34.7818)
            )
            dbRef.updateChildren(updates)
        }
    }
}
