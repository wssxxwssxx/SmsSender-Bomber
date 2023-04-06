package com.example.smsbomber.di

import android.app.Application
import android.content.Context
import com.example.smsbomber.data.SmsRepositoryImpl
import com.example.smsbomber.domain.SendSmsUseCase
import com.example.smsbomber.domain.SmsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    fun provideSmsRepository(context: Context): SmsRepository = SmsRepositoryImpl(context)

    @Provides
    fun provideSendSmsUseCase(smsRepository: SmsRepository): SendSmsUseCase = SendSmsUseCase(smsRepository)
}
