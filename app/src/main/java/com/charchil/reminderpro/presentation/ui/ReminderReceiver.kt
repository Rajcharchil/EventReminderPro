package com.charchil.reminderpro.presentation.ui

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.charchil.reminderpro.CHANNEL
import com.charchil.reminderpro.R
import com.charchil.reminderpro.domain.model.Reminder
import com.charchil.reminderpro.domain.use_case.UpdateUseCase
import com.charchil.reminderpro.util.REMINDER
import com.charchil.reminderpro.util.DONE
import com.charchil.reminderpro.util.REJECT
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var updateUseCase: UpdateUseCase
    private lateinit var mediaPlayer: MediaPlayer

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val reminderJson = intent.getStringExtra(REMINDER)
        val reminder = Gson().fromJson(reminderJson, Reminder::class.java)

        Log.d("ReminderReceiver", "Action received: $action")

        when (action) {
            DONE -> {
                runBlocking {
                    updateUseCase.invoke(reminder.copy(isTaken = true))
                }
                Toast.makeText(context, "Marked as DONE", Toast.LENGTH_SHORT).show()

                // Cancel the notification
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.cancel(reminder.timeInmillis.toInt())
                return
            }

            REJECT -> {
                runBlocking {
                    updateUseCase.invoke(reminder.copy(isTaken = false))
                }
                Toast.makeText(context, "Reminder CLOSED", Toast.LENGTH_SHORT).show()

                // Cancel the notification
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.cancel(reminder.timeInmillis.toInt())
                return
            }

            else -> {
                // Trigger alarm sound
                mediaPlayer = MediaPlayer.create(context, R.raw.alarm_music)
                mediaPlayer.setOnCompletionListener { mediaPlayer.release() }
                mediaPlayer.start()

                // DONE button intent
                val doneIntent = Intent(context, ReminderReceiver::class.java).apply {
                    putExtra(REMINDER, reminderJson)
                    setAction(DONE)
                }

                val donePendingIntent = PendingIntent.getBroadcast(
                    context,
                    reminder.timeInmillis.toInt(),
                    doneIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                // CLOSE button intent
                val closeIntent = Intent(context, ReminderReceiver::class.java).apply {
                    putExtra(REMINDER, reminderJson)
                    setAction(REJECT)
                }

                val closePendingIntent = PendingIntent.getBroadcast(
                    context,
                    (reminder.timeInmillis + 1).toInt(),
                    closeIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                // Build notification
                val notificationBuilder = NotificationCompat.Builder(context, CHANNEL)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Event Reminder")
                    .setContentText("${reminder.name} - ${reminder.dosage}")
                    .addAction(R.drawable.ic_check, "Done", donePendingIntent)
                    .addAction(R.drawable.ic_close, "Close", closePendingIntent)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)

                // Show notification
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(context, POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        NotificationManagerCompat.from(context)
                            .notify(reminder.timeInmillis.toInt(), notificationBuilder.build())
                    }
                } else {
                    NotificationManagerCompat.from(context)
                        .notify(reminder.timeInmillis.toInt(), notificationBuilder.build())
                }

                Toast.makeText(context, "Reminder Triggered!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
