package com.example.calldialog

import android.content.Intent
import android.media.AudioManager
import android.media.RingtoneManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class CallDialogActivity : AppCompatActivity() {

    private lateinit var tvCallerName: TextView
    private lateinit var tvCallerNumber: TextView
    private lateinit var tvCallStatus: TextView
    private lateinit var ivCallerImage: ImageView
    private lateinit var btnAnswer: Button
    private lateinit var btnReject: Button
    private var callDurationSeconds = 0
    private lateinit var handler: Handler
    private var isCallActive = false
    private var ringtoneMediaPlayer: android.media.Ringtone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_dialog)

        // Initialize views
        tvCallerName = findViewById(R.id.tvCallerName)
        tvCallerNumber = findViewById(R.id.tvCallerNumber)
        tvCallStatus = findViewById(R.id.tvCallStatus)
        ivCallerImage = findViewById(R.id.ivCallerImage)
        btnAnswer = findViewById(R.id.btnAnswer)
        btnReject = findViewById(R.id.btnReject)

        handler = Handler(Looper.getMainLooper())

        // Get intent data
        val callerName = intent.getStringExtra("CALLER_NAME") ?: "Unknown"
        val callerNumber = intent.getStringExtra("CALLER_NUMBER") ?: "Unknown"

        // Set caller information
        tvCallerName.text = callerName
        tvCallerNumber.text = callerNumber
        tvCallStatus.text = "Incoming Call..."

        // Load default caller image
        loadCallerImage()

        // Play ringtone
        playRingtone()

        // Answer button
        btnAnswer.setOnClickListener {
            answerCall()
        }

        // Reject button
        btnReject.setOnClickListener {
            rejectCall()
        }

        // Auto-reject after 30 seconds if not answered
        handler.postDelayed({
            if (!isCallActive) {
                rejectCall()
            }
        }, 30000)
    }

    private fun loadCallerImage() {
        // Load default caller image from drawable
        Glide.with(this)
            .load(R.drawable.ic_default_caller)
            .placeholder(R.drawable.ic_default_caller)
            .error(R.drawable.ic_default_caller)
            .circleCrop()
            .into(ivCallerImage)
    }

    private fun playRingtone() {
        try {
            val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ringtoneMediaPlayer = RingtoneManager.getRingtone(this, ringtoneUri)
            ringtoneMediaPlayer?.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopRingtone() {
        try {
            ringtoneMediaPlayer?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun answerCall() {
        stopRingtone()
        isCallActive = true
        tvCallStatus.text = "Call Connected"
        btnAnswer.isEnabled = false
        btnReject.text = "End Call"

        // Enable speaker
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager.isSpeakerphoneOn = true

        // Start call duration timer
        startCallTimer()
    }

    private fun rejectCall() {
        stopRingtone()
        handler.removeCallbacksAndMessages(null)
        finish()
    }

    private fun startCallTimer() {
        handler.post(object : Runnable {
            override fun run() {
                callDurationSeconds++
                val minutes = callDurationSeconds / 60
                val seconds = callDurationSeconds % 60
                tvCallStatus.text = String.format("Connected - %02d:%02d", minutes, seconds)
                handler.postDelayed(this, 1000)
            }
        })
    }

    override fun onBackPressed() {
        // Prevent back button from closing the dialog during call
        if (isCallActive) {
            rejectCall()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRingtone()
        handler.removeCallbacksAndMessages(null)
    }
}
