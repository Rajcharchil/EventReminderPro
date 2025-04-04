package com.charchil.reminderpro.domain.use_case

import com.charchil.reminderpro.domain.repository.ReminderRepository
import javax.inject.Inject

class GetAllReminderUseCase @Inject constructor(private val reminderRepository: ReminderRepository) {
    operator fun invoke() = reminderRepository.getAllReminders()

}