package id.co.ingatin.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.co.ingatin.platform.notification.AlarmReminderSchedulerImpl
import id.co.ingatin.platform.notification.ReminderLedger
import id.co.ingatin.platform.notification.ReminderScheduler
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object NotificationModule {

    @Provides
    @Singleton
    fun provideReminderLedger(
        @ApplicationContext context: Context
    ): ReminderLedger = ReminderLedger(context)

    @Provides
    @Singleton
    fun provideReminderScheduler(
        @ApplicationContext context: Context,
        ledger: ReminderLedger
    ): ReminderScheduler = AlarmReminderSchedulerImpl(context, ledger)
}
