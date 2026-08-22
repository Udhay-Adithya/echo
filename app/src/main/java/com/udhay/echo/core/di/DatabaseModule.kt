package com.udhay.echo.core.di

import android.content.Context
import com.udhay.echo.core.database.AppDatabase
import com.udhay.echo.feature.chat.data.local.ChatDao
import com.udhay.echo.feature.settings.data.local.UserSettingsDao
import com.udhay.echo.feature.tools.data.local.ToolDao
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

    @Single
    fun provideToolDao(db: AppDatabase): ToolDao =
        db.toolDao()
}