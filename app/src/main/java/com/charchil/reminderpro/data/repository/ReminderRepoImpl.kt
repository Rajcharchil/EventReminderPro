package com.charchil.reminderpro.data.repository

import com.charchil.reminderpro.data.local.ReminderDao
import com.charchil.reminderpro.domain.model.Reminder
import com.charchil.reminderpro.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow

class ReminderRepoImpl (private val reminderDao: ReminderDao): ReminderRepository {
    override suspend fun insert(reminder: Reminder) {
        reminderDao.insert(reminder)
    }

    override suspend fun update(reminder: Reminder) {
        reminderDao.update(reminder)
    }

    override suspend fun delete(reminder: Reminder) {
        reminderDao.delete(reminder)
    }

    override fun getAllReminders(): Flow<List<Reminder>> = reminderDao.getAllReminder()

}