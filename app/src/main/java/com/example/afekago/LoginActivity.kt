package com.example.afekago

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 🔹 בדיקה אם Firebase מאותחל
        try {
            FirebaseApp.initializeApp(this)
            Log.d("FirebaseCheck", "✅ Firebase אותחל בהצלחה!")
        } catch (e: Exception) {
            Log.e("FirebaseCheck", "❌ שגיאה באתחול Firebase: ${e.message}")
        }

        // 🔹 בדיקה אם ה-Database מחובר
        try {
            val database = FirebaseDatabase.getInstance()
            database.reference.child("test").setValue("test")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseDatabase", "✅ חיבור ל-Firebase Database תקין!")
                    } else {
                        Log.e("FirebaseDatabase", "❌ שגיאה בחיבור ל-Database: ${task.exception?.message}")
                    }
                }
        } catch (e: Exception) {
            Log.e("FirebaseDatabase", "❌ שגיאה בגישה ל-Firebase Database: ${e.message}")
        }

        auth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<EditText>(R.id.etEmail)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val loginButton = findViewById<MaterialButton>(R.id.btnLogin)
        val registerButton = findViewById<MaterialButton>(R.id.btnRegister)

        // התחברות למערכת
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔹 בדיקת חיבור ל-Firebase Authentication
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseAuth", "✅ התחברות הצליחה עבור: ${auth.currentUser?.email}")
                        Toast.makeText(this, "התחברות מוצלחת!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Log.e("FirebaseAuth", "❌ שגיאת התחברות: ${task.exception?.message}")
                        Toast.makeText(this, "התחברות נכשלה: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // מעבר למסך הרשמה
        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
