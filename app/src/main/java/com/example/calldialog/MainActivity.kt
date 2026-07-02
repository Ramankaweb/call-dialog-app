package com.example.calldialog

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var etCallerName: EditText
    private lateinit var etCallerNumber: EditText
    private lateinit var btnStartCall: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etCallerName = findViewById(R.id.etCallerName)
        etCallerNumber = findViewById(R.id.etCallerNumber)
        btnStartCall = findViewById(R.id.btnStartCall)

        // Set default values
        etCallerName.setText("Raj Kumar")
        etCallerNumber.setText("+91 9876543210")

        btnStartCall.setOnClickListener {
            val callerName = etCallerName.text.toString().trim()
            val callerNumber = etCallerNumber.text.toString().trim()

            if (callerName.isEmpty() || callerNumber.isEmpty()) {
                Toast.makeText(this, "Please enter caller name and number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Start call dialog activity
            val intent = Intent(this, CallDialogActivity::class.java).apply {
                putExtra("CALLER_NAME", callerName)
                putExtra("CALLER_NUMBER", callerNumber)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }
}
