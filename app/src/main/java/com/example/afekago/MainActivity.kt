package com.example.afekago

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.afekago.models.RideRequest
import com.example.afekago.models.User
import com.example.afekago.screens.DriverViewModel
import com.example.afekago.ui.theme.AfekaGoTheme
import com.example.afekago.utils.findNearbyUsers
import com.example.afekago.utils.getCurrentUserData
import com.example.afekago.utils.sendRideRequest
import com.example.afekago.utils.updateRideRequestStatus
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.maps.android.compose.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user == null) {
            redirectToLogin()
        } else {
            val database = FirebaseDatabase.getInstance().getReference("users").child(user.uid)
            database.get().addOnCompleteListener { task ->
                if (!task.isSuccessful || !task.result.exists()) {
                    auth.signOut()
                    redirectToLogin()
                } else {
                    loadUserData()
                    setContent {
                        AfekaGoTheme {
                            val driverViewModel: DriverViewModel = viewModel()
                            MainScreen(activity = this, viewModel = driverViewModel)
                        }
                    }
                }
            }
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun loadUserData() {
        val database = FirebaseDatabase.getInstance().reference.child("users")
        database.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase", "User data loaded successfully")
            } else {
                Log.e("Firebase", "Failed to load user data: ${task.exception}")
            }
        }
    }
}


@Composable
fun MainScreen(activity: ComponentActivity, viewModel: DriverViewModel) {
    var userMode by remember { mutableStateOf("Driver") }
    val userName = getUserName()
    var nearbyUsers by remember { mutableStateOf(emptyList<User>()) }
    val rideRequests by viewModel.rideRequests.observeAsState(emptyList())

    LaunchedEffect(userMode) {
        findNearbyUsers(if (userMode == "Driver") "Passenger" else "Driver", 15) { users ->
            nearbyUsers = users
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                userName = userName,
                activity = activity,
                nearbyUsers = nearbyUsers,
                navigateToSettings = {
                    val intent = Intent(activity, SettingsActivity::class.java)
                    activity.startActivity(intent)
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(userMode) { newMode ->
                userMode = newMode
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            DisplayUsersOnMap(nearbyUsers)
            if (userMode == "Driver") {
                RideRequestList(viewModel)
            } else {
                AvailableDriversList(nearbyUsers, activity)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    userName: String,
    activity: ComponentActivity,
    nearbyUsers: List<User>,
    navigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50)),
        title = {
            Text(
                text = "Welcome, $userName",
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(activity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                activity.startActivity(intent)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_logout),
                    contentDescription = "Logout",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = {
                val intent = Intent(activity, PlanTripActivity::class.java)
                activity.startActivity(intent)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Plan Trip",
                    tint = Color.White
                )
            }

            // ✅ NEW: Start Ride Icon
            IconButton(onClick = {
                if (nearbyUsers.isNotEmpty()) {
                    getCurrentUserData { passenger ->
                        if (passenger != null) {
                            val driver = nearbyUsers.first()
                            val intent = Intent(context, NavigationActivity::class.java)
                            intent.putExtra("driverId", driver.id)
                            intent.putExtra("passengerId", passenger.id)
                            context.startActivity(intent)
                        }
                    }
                } else {
                    Toast.makeText(context, "אין נהגים או נוסעים מתאימים", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_start_ride),
                    contentDescription = "Start Ride",
                    tint = Color.White
                )
            }

            IconButton(onClick = { navigateToSettings() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu),
                    contentDescription = "Menu",
                    tint = Color.White
                )
            }
        }
    )
}


@Composable
fun DisplayUsersOnMap(users: List<User>) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(32.0853, 34.7818), 12f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        users.forEach { user ->
            Marker(
                state = MarkerState(position = LatLng(user.latitude, user.longitude)),
                title = user.name,
                snippet = user.phoneNumber
            )
        }
    }
}

@Composable
fun BottomNavigationBar(selectedMode: String, onModeSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(5.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ModeButton(Icons.Outlined.DirectionsCar, selectedMode == "Driver") { onModeSelected("Driver") }
        ModeButton(Icons.Outlined.People, selectedMode == "Join") { onModeSelected("Join") }
    }
}

@Composable
fun ModeButton(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF4CAF50) else Color.Transparent,
            contentColor = if (isSelected) Color.White else Color(0xFF4CAF50)
        ),
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .height(60.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(32.dp))
    }
}

@Composable
fun RideRequestList(viewModel: DriverViewModel) {
    val rideRequests by viewModel.rideRequests.observeAsState(emptyList())

    Column {
        rideRequests.forEach { request ->
            RideRequestCard(
                request = request,
                onAccept = { updateRideRequestStatus(request.requestId, "accepted") },
                onReject = { updateRideRequestStatus(request.requestId, "rejected") }
            )
        }
    }
}

@Composable
fun RideRequestCard(request: RideRequest, onAccept: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = "Passenger: ${request.passengerName}")
            Text(text = "Phone: ${request.passengerPhone}")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = onAccept, colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) {
                    Text("Accept")
                }
                Button(onClick = onReject, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("Reject")
                }
            }
        }
    }
}

@Composable
fun AvailableDriversList(drivers: List<User>, activity: ComponentActivity) {
    val context = LocalContext.current

    Column {
        drivers.forEach { driver ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        Toast
                            .makeText(context, "הנסיעה אושרה!", Toast.LENGTH_SHORT)
                            .show()

                        getCurrentUserData { passenger ->
                            if (passenger != null) {
                                val intent = Intent(context, NavigationActivity::class.java)
                                intent.putExtra("driverId", driver.id)
                                intent.putExtra("passengerId", passenger.id)
                                context.startActivity(intent)
                            }
                        }
                    },
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Driver: ${driver.name}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        "Phone: ${driver.phoneNumber}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Button(onClick = {
                        Toast
                            .makeText(context, "הנסיעה אושרה!", Toast.LENGTH_SHORT)
                            .show()

                        getCurrentUserData { passenger ->
                            if (passenger != null) {
                                val intent = Intent(context, NavigationActivity::class.java)
                                intent.putExtra("driverId", driver.id)
                                intent.putExtra("passengerId", passenger.id)
                                context.startActivity(intent)
                            }
                        }
                    }) {
                        Text("Request Ride")
                    }
                }
            }
        }
    }
}




@Composable
fun getUserName(): String {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    return when {
        user?.displayName != null -> user.displayName!!
        user?.email != null -> user.email!!.substringBefore("@")
        else -> "Guest"
    }
}