package com.example.afekago

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<EditText>(R.id.etRegisterEmail)
        val passwordEditText = findViewById<EditText>(R.id.etRegisterPassword)
        val nameEditText = findViewById<EditText>(R.id.etRegisterName)
        val phoneEditText = findViewById<EditText>(R.id.etRegisterPhone)
        val registerButton = findViewById<MaterialButton>(R.id.btnRegisterUser)
        val backToLoginButton = findViewById<MaterialButton>(R.id.btnBackToLogin)

        backToLoginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val name = nameEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val userId = user?.uid

                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                        user?.updateProfile(profileUpdates)

                        val database = FirebaseDatabase.getInstance().getReference("users")
                        val userData = mapOf(
                            "email" to email,
                            "name" to name,
                            "phone" to phone,
                            "radius" to 10,
                            "passengers" to 2,
                            "city" to "תל אביב",
                            "notifications" to true
                        )

                        userId?.let {
                            database.child(it).setValue(userData)
                        }

                        Toast.makeText(this, "נרשמת בהצלחה!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "הרשמה נכשלה: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
