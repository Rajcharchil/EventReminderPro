package com.charchil.reminderpro.data.di

import android.content.Context
import com.charchil.reminderpro.data.local.ReminderDatabase
import com.charchil.reminderpro.data.local.ReminderDao
import com.charchil.reminderpro.data.repository.ReminderRepoImpl
import com.charchil.reminderpro.domain.repository.ReminderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import kotlin.text.Typography.dagger

@InstallIn(SingletonComponent::class)
@Module
object DataModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): ReminderDatabase {
            return ReminderDatabase.getInstace(context)
    }
    @Provides
    fun provideReminderDao(reminderDatabase: ReminderDatabase): ReminderDao {
        return reminderDatabase.getReminderDao()
    }

    @Provides
    fun provideReminderRepository(reminderDao: ReminderDao): ReminderRepository {
        return ReminderRepoImpl(reminderDao)
    }



}
