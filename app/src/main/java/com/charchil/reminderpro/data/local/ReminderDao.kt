package com.charchil.reminderpro.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.charchil.reminderpro.domain.model.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  inset(reminder: Reminder)

    @Delete
    suspend fun delete(reminder: Reminder)

    @Update
    suspend fun update(reminder: Reminder)

    @Query("SELECT * FROM reminder ORDER BY timeInmillis DESC")
    fun getAllReminder(): Flow<List<Reminder>>


}