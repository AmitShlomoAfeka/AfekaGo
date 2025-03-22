package com.example.afekago

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PaymentSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_success)

        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val tvBalance = findViewById<TextView>(R.id.tvBalance)
        val btnBackToMain = findViewById<Button>(R.id.btnBackToMain)

        // סימולציה של יתרה לאחר תשלום
        val currentBalance = 33.7
        tvBalance.text = "current balance: $currentBalance ₪"

        // אפשרות לדרג את הנהג
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            // אפשר לשלוח את הדירוג ל-Firebase אם רוצים
        }

        // מעבר חזרה למסך הראשי
        btnBackToMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // סוגר את המסך הנוכחי
        }
    }
}
