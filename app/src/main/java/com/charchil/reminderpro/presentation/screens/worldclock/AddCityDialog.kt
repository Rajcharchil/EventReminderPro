package com.charchil.reminderpro.presentation.screens.worldclock

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Toast
import com.charchil.reminderpro.databinding.DialogAddCityBinding
import java.util.*

class AddCityDialog(context: Context) : Dialog(context) {
    private lateinit var binding: DialogAddCityBinding
    private var onCitySelected: ((String, String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogAddCityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTimeZoneSpinner()
        setupButtons()
    }

    private fun setupTimeZoneSpinner() {
        val timeZones = TimeZone.getAvailableIDs()
            .filter { it.contains("/") }
            .map { it.split("/").last() }
            .sorted()

        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, timeZones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.timeZoneSpinner.adapter = adapter
    }

    private fun setupButtons() {
        binding.btnAdd.setOnClickListener {
            val cityName = binding.cityNameEditText.text.toString().trim()
            val timeZone = binding.timeZoneSpinner.selectedItem as String

            if (cityName.isEmpty()) {
                Toast.makeText(context, "Please enter a city name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onCitySelected?.invoke(cityName, timeZone)
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    fun setOnCitySelected(listener: (String, String) -> Unit) {
        onCitySelected = listener
    }
} 