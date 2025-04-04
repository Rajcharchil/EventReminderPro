package com.charchil.reminderpro.presentation.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.charchil.reminderpro.domain.model.Reminder
import com.google.gson.Gson
import com.charchil.reminderpro.util.REMINDER


const val REMINDER = "REMINDER"
fun setUpAlarm(context: Context,reminder:Reminder){
    val intent = Intent(context,ReminderReceiver::class.java).apply {
        putExtra(REMINDER, Gson().toJson(reminder))
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminder.timeInmillis.toInt(),
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    try {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminder.timeInmillis,
            pendingIntent
        )

    }catch (e:SecurityException){
        e.printStackTrace()
    }

}

fun cancleAlarm(context: Context,reminder:Reminder){
    val intent = Intent(context,ReminderReceiver::class.java).apply {
        putExtra(REMINDER, Gson().toJson(reminder))
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminder.timeInmillis.toInt(),
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    try {
        alarmManager.cancel(pendingIntent)

    }catch (e:SecurityException){
        e.printStackTrace()
    }

}

fun setUpPeriodicAlarm(context: Context,reminder:Reminder){
    val intent = Intent(context,ReminderReceiver::class.java).apply {
        putExtra(REMINDER, Gson().toJson(reminder))
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reminder.timeInmillis.toInt(),
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    try {
        val interval = 2L * 60L * 1000L
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,reminder.timeInmillis,interval,pendingIntent)

    }catch (e:SecurityException){
        e.printStackTrace()
    }

}