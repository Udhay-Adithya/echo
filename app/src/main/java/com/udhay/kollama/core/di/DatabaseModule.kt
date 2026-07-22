package com.udhay.kollama.core.di

import android.content.Context
import com.udhay.kollama.core.database.AppDatabase
import com.udhay.kollama.feature.chat.data.local.ChatDao
import com.udhay.kollama.feature.settings.data.local.UserSettingsDao
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class DatabaseModule {

    @Single
    fun provideAppDatabase(context: Context): AppDatabase = AppDatabase.create(context)

    @Single
    fun provideUserSettingsDao(db: AppDatabase): UserSettingsDao =
        db.userSettingsDao()

    @Single
    fun provideChatDao(db: AppDatabase): ChatDao =
        db.chatDao()
}