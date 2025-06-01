package com.charchil.reminderpro.presentation.screens.worldclock

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.charchil.reminderpro.R
import com.charchil.reminderpro.databinding.ActivityWorldClockBinding
import com.charchil.reminderpro.presentation.adapters.WorldClockAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.charchil.reminderpro.data.api.WeatherResponse
import com.charchil.reminderpro.data.api.WeatherService
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import retrofit2.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.ImageButton
import android.widget.ImageView


class WorldClockActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWorldClockBinding
    private lateinit var adapter: WorldClockAdapter
    private val cities = mutableListOf(
        City("India \uD83C\uDDEE\uD83C\uDDF3", "Asia/Kolkata"),
        City("New York \uD83C\uDDF3\uD83C\uDDE8", "America/New_York"),
        City("London \uD83C\uDFF4\uDB40\uDC67\uDB40\uDC62\uDB40\uDC65\uDB40\uDC6E\uDB40\uDC67\uDB40\uDC7F", "Europe/London"),
        City("Tokyo", "Asia/Tokyo"),
        City("Sydney", "Australia/Sydney")
    )
    private val timer = Timer()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var textTemp: TextView
    private lateinit var textCondition: TextView
    private lateinit var textLocationTime: TextView
    private val apiKey = "4dc9a7f4fa83250cf10ef01197bf087b" // 🔑 Replace this with your OpenWeatherMap API key
    private var weatherTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorldClockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize weather views
        textTemp = findViewById(R.id.textTemperature)
        textCondition = findViewById(R.id.textWeatherCondition)
        textLocationTime = findViewById(R.id.textLocationTime)

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Get location and weather
        getLocationAndWeather()

        setupToolbar()
        setupRecyclerView()
        setupSearchBar()
        setupFab()
        startClockRefresh()
        startWeatherAutoRefresh()

//        findViewById<ImageButton>(R.id.btnRefreshWeather).setOnClickListener {
//            getLocationAndWeather()
//        }
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
        val searchView = binding.searchBar

        // Yeh naya code: Jaise hi click ho, search view expand ho jaye
        searchView.setOnQueryTextFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                searchView.isIconified = false
            }
        }

        // Pehle se jo search text filter tha
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
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

    private fun getLocationAndWeather() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                fetchWeather(it.latitude, it.longitude)
            }
        }
    }

    private fun fetchWeather(lat: Double, lon: Double) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherService = retrofit.create(WeatherService::class.java)
        val call = weatherService.getWeather(lat, lon, apiKey, "metric")

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                val data = response.body()
                if (data != null) {
                    textTemp.text = "Temp: ${data.main.temp}°C"
                    textCondition.text = "Condition: ${data.weather[0].description}"
                    val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                    textLocationTime.text = "📍Location: ${data.name}    🕑Time: $time"
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                textTemp.text = "Failed to load weather"
            }
        })
    }

    private fun refreshWeather() {
        // TODO: Replace with your actual weather fetching logic
        // Example: Simulate update
        findViewById<TextView>(R.id.textTemperature).text = "34°C"
        findViewById<TextView>(R.id.textWeatherCondition).text = "Haze"
//        findViewById<TextView>(R.id.textHighLow).text = "H39° L31°"
        findViewById<TextView>(R.id.textLocationTime).text = "Patna, BR 09:36"
        findViewById<ImageView>(R.id.imgWeatherIcon).setImageResource(R.drawable.ic_weather_sunny)
    }

    private fun startWeatherAutoRefresh() {
        weatherTimer?.cancel()
        weatherTimer = Timer()
        weatherTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    getLocationAndWeather()
                }
            }
        }, 0, 5 * 60 * 1000) // every 5 minutes
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
        weatherTimer?.cancel()
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