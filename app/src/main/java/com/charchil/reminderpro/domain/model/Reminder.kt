package com.charchil.reminderpro.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Reminder(

    val name:String,
    val dosage:String,
    @PrimaryKey(autoGenerate = false)
    val timeInmillis:Long,
    val isTaken:Boolean=false,
    val isRepeat:Boolean=false,

)



