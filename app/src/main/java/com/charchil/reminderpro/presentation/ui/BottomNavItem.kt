package com.charchil.reminderpro.presentation.ui

import androidx.annotation.DrawableRes
import com.charchil.reminderpro.R

sealed class BottomNavItem(val title: String, @DrawableRes val icon: Int) {
    object Home : BottomNavItem("Alarm", R.drawable.alarm_icon)
    object Reminder : BottomNavItem("World Clock", R.drawable.worldclock)
    object Settings : BottomNavItem("Stopwatch", R.drawable.stopwatch)
}
