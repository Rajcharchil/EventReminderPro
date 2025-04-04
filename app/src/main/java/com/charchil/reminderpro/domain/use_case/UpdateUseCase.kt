package com.charchil.reminderpro.domain.use_case

import com.charchil.reminderpro.domain.model.Reminder
import com.charchil.reminderpro.domain.repository.ReminderRepository
import javax.inject.Inject

class UpdateUseCase @Inject constructor(private val reminderRepository: ReminderRepository) {
    suspend operator fun invoke(reminder: Reminder) = reminderRepository.update(reminder)

}