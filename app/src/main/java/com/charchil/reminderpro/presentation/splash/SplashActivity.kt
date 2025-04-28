package com.charchil.reminderpro.presentation.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.charchil.reminderpro.MainActivity
import com.charchil.reminderpro.presentation.ui.theme.ReminderProTheme

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReminderProTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    SplashScreen(
                        onSplashFinished = {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    )
                }
            }
        }
    }
} 