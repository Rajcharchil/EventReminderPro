package com.charchil.reminderpro.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.charchil.reminderpro.databinding.ItemWorldClockBinding
import com.charchil.reminderpro.presentation.screens.worldclock.WorldClockActivity.City
import java.text.SimpleDateFormat
import java.util.*

class WorldClockAdapter(private var cities: List<City>) : ListAdapter<City, WorldClockAdapter.WorldClockViewHolder>(CityDiffCallback()) {

    private var filteredCities = cities.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorldClockViewHolder {
        val binding = ItemWorldClockBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WorldClockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorldClockViewHolder, position: Int) {
        holder.bind(filteredCities[position])
    }

    override fun getItemCount(): Int = filteredCities.size

    fun updateCities(newCities: List<City>) {
        cities = newCities
        filteredCities = newCities.toMutableList()
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredCities = if (query.isEmpty()) {
            cities.toMutableList()
        } else {
            cities.filter { it.name.contains(query, ignoreCase = true) }.toMutableList()
        }
        notifyDataSetChanged()
    }

    class WorldClockViewHolder(private val binding: ItemWorldClockBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(city: City) {
            binding.apply {
                cityNameTextView.text = city.name
                timeTextView.text = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    .apply { timeZone = TimeZone.getTimeZone(city.timeZone) }
                    .format(Date())
                timeZoneTextView.text = city.timeZone
            }
        }
    }

    private class CityDiffCallback : DiffUtil.ItemCallback<City>() {
        override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem == newItem
        }
    }
} 