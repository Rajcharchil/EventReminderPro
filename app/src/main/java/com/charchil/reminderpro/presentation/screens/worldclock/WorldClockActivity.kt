package com.charchil.reminderpro.presentation.screens.worldclock

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.charchil.reminderpro.R
import com.charchil.reminderpro.databinding.ActivityWorldClockBinding
import com.charchil.reminderpro.presentation.adapters.WorldClockAdapter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class WorldClockActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWorldClockBinding
    private lateinit var adapter: WorldClockAdapter
    private val cities = mutableListOf(
        City("New York", "America/New_York"),
        City("London", "Europe/London"),
        City("Tokyo", "Asia/Tokyo"),
        City("Sydney", "Australia/Sydney")
    )
    private val timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorldClockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSearchBar()
        setupFab()
        startClockRefresh()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupRecyclerView() {
        adapter = WorldClockAdapter(cities)
        binding.worldClocksRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@WorldClockActivity)
            adapter = this@WorldClockActivity.adapter
        }
    }

    private fun setupSearchBar() {
        binding.searchBar.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText.orEmpty())
                return true
            }
        })
    }

    private fun setupFab() {
        binding.fabAddClock.setOnClickListener {
            showAddCityDialog()
        }
    }

    private fun showAddCityDialog() {
        val dialog = AddCityDialog(this)
        dialog.setOnCitySelected { cityName: String, timeZone: String ->
            if (cities.none { it.name == cityName }) {
                cities.add(City(cityName, timeZone))
                adapter.updateCities(cities)
                Toast.makeText(this, "City added successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "City already exists", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun startClockRefresh() {
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
            }
        }, 0, 1000) // Update every second
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_world_clock, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> {
                cities.sortBy { it.name }
                adapter.updateCities(cities)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    data class City(
        var name: String,
        var timeZone: String
    ) {
        val currentTime: String
            get() = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                .apply { timeZone = TimeZone.getTimeZone(this@City.timeZone) }
                .format(Date())
    }
}