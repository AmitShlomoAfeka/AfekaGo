package com.example.afekago

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PaymentMethodActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)

        // קבלת רפרנס לכפתורים
        val btnCreditCard: ImageView = findViewById(R.id.btnCreditCard)
        val btnBit: ImageView = findViewById(R.id.btnBit)
        val btnCash: ImageView = findViewById(R.id.btnCash)
        val btnBack: ImageView = findViewById(R.id.btnBack)

        // מאזינים ללחיצות על אמצעי תשלום
        val paymentClickListener = View.OnClickListener {
            val intent = Intent(this, PaymentSuccessActivity::class.java)
            startActivity(intent)
        }

        btnCreditCard.setOnClickListener(paymentClickListener)
        btnBit.setOnClickListener(paymentClickListener)
        btnCash.setOnClickListener(paymentClickListener)

        // לחיצה על חזור מחזירה למסך הראשי
        btnBack.setOnClickListener {
            finish()
        }
    }
}
