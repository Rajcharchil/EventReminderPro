package com.charchil.reminderpro.util

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.charchil.reminderpro.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import androidx.recyclerview.widget.DefaultItemAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import android.util.Log

class StopwatchActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var stopButton: MaterialButton
    private lateinit var resetButton: MaterialButton
    private lateinit var lapButton: MaterialButton
    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar

    private var isRunning = false
    private var startTime = 0L
    private var elapsedTime = 0L
    private val lapTimes = mutableListOf<LapTime>()
    private lateinit var lapAdapter: LapAdapter
    private var lastLapTime = 0L

    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                val currentTime = System.currentTimeMillis()
                val updatedTime = elapsedTime + (currentTime - startTime)
                updateDisplay(updatedTime)
                handler.postDelayed(this, 16) // ~60 FPS update rate
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stopwatch)
        
        initializeViews()
        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        
        // Initial state
        lapButton.isEnabled = false
        progressIndicator.max = 100
    }

    private fun initializeViews() {
        timerTextView = findViewById(R.id.timerTextView)
        stopButton = findViewById(R.id.stopButton)
        resetButton = findViewById(R.id.resetButton)
        lapButton = findViewById(R.id.lapButton)
        progressIndicator = findViewById(R.id.progressIndicator)
        recyclerView = findViewById(R.id.lapTimesRecyclerView)
        toolbar = findViewById(R.id.toolbar)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        lapAdapter = LapAdapter(lapTimes)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@StopwatchActivity).apply {
                reverseLayout = true
                stackFromEnd = true
            }
            adapter = lapAdapter
            itemAnimator = DefaultItemAnimator()
        }
    }

    private fun setupClickListeners() {
        stopButton.setOnClickListener {
            if (!isRunning) {
                // Start
                startTime = System.currentTimeMillis()
                handler.post(updateRunnable)
                isRunning = true
                stopButton.text = "Stop"
                stopButton.setBackgroundColor(getColor(android.R.color.holo_red_light))
                lapButton.isEnabled = true
            } else {
                // Stop
                elapsedTime += System.currentTimeMillis() - startTime
                handler.removeCallbacks(updateRunnable)
                isRunning = false
                stopButton.text = "Start"
                stopButton.setBackgroundColor(getColor(android.R.color.holo_green_light))
            }
        }

        resetButton.setOnClickListener {
            handler.removeCallbacks(updateRunnable)
            isRunning = false
            startTime = 0L
            elapsedTime = 0L
            lastLapTime = 0L
            lapTimes.clear()
            lapAdapter.notifyDataSetChanged()
            updateDisplay(0L)
            stopButton.text = "Start"
            stopButton.setBackgroundColor(getColor(android.R.color.holo_green_light))
            lapButton.isEnabled = false
            progressIndicator.progress = 0
        }

        lapButton.setOnClickListener {
            if (isRunning) {
                val currentTime = System.currentTimeMillis()
                val totalTime = elapsedTime + (currentTime - startTime)
                val lapDifference = totalTime - lastLapTime
                
                lapTimes.add(LapTime(
                    lapNumber = lapTimes.size + 1,
                    totalTime = totalTime,
                    lapDifference = lapDifference
                ))
                
                lastLapTime = totalTime
                lapAdapter.notifyItemInserted(lapTimes.size - 1)
                recyclerView.scrollToPosition(lapTimes.size - 1)
                
                Log.d("StopwatchActivity", "Added lap: ${lapTimes.last()}")
            }
        }
    }

    private fun updateDisplay(timeInMillis: Long) {
        timerTextView.text = formatTime(timeInMillis)
        // Update progress (assuming 1 minute = full circle)
        val progress = ((timeInMillis % 60000) / 600).toInt()
        progressIndicator.progress = progress
    }

    private fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        val centiseconds = (ms % 1000) / 10
        return String.format("%02d:%02d.%02d", minutes, seconds, centiseconds)
    }

    data class LapTime(
        val lapNumber: Int,
        val totalTime: Long,
        val lapDifference: Long
    )

    private inner class LapAdapter(private val laps: List<LapTime>) : 
            RecyclerView.Adapter<LapAdapter.LapViewHolder>() {

        inner class LapViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val lapNumberText: TextView = view.findViewById(R.id.lapNumberText)
            val lapTimeText: TextView = view.findViewById(R.id.lapTimeText)
            val lapDifferenceText: TextView = view.findViewById(R.id.lapDifferenceText)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LapViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_lap_time, parent, false)
            return LapViewHolder(view)
        }

        override fun onBindViewHolder(holder: LapViewHolder, position: Int) {
            val lap = laps[position]
            holder.lapNumberText.text = "Lap ${lap.lapNumber}"
            holder.lapTimeText.text = formatTime(lap.totalTime)
            holder.lapDifferenceText.text = if (lap.lapDifference > 0) {
                "+${formatTime(lap.lapDifference)}"
            } else {
                formatTime(lap.lapDifference)
            }
        }

        override fun getItemCount() = laps.size
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
    }
}