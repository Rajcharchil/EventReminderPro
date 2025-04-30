package com.charchil.reminderpro.util



import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.charchil.reminderpro.R

class StopwatchActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var startButton: Button
    private lateinit var pauseButton: Button
    private lateinit var resetButton: Button

    private var isRunning = false
    private var startTime = 0L
    private var elapsedTime = 0L

    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            val currentTime = System.currentTimeMillis()
            val updatedTime = elapsedTime + (currentTime - startTime)
            timerTextView.text = formatTime(updatedTime)
            handler.postDelayed(this, 100)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stopwatch)

        timerTextView = findViewById(R.id.timerTextView)
        startButton = findViewById(R.id.startButton)
        pauseButton = findViewById(R.id.pauseButton)
        resetButton = findViewById(R.id.resetButton)

        startButton.setOnClickListener {
            if (!isRunning) {
                startTime = System.currentTimeMillis()
                handler.post(updateRunnable)
                isRunning = true
            }
        }

        pauseButton.setOnClickListener {
            if (isRunning) {
                elapsedTime += System.currentTimeMillis() - startTime
                handler.removeCallbacks(updateRunnable)
                isRunning = false
            }
        }

        resetButton.setOnClickListener {
            handler.removeCallbacks(updateRunnable)
            isRunning = false
            startTime = 0L
            elapsedTime = 0L
            timerTextView.text = formatTime(0L)
        }
    }

    private fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        val milliseconds = (ms % 1000) / 10
        return String.format("%02d:%02d:%02d.%02d", hours, minutes, seconds, milliseconds)
    }
}