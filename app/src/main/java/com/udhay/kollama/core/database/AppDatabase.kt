package com.udhay.kollama.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.udhay.kollama.feature.chat.data.local.ChatDao
import com.udhay.kollama.feature.chat.data.local.ChatEntity
import com.udhay.kollama.feature.chat.data.local.ChatMessageEntity
import com.udhay.kollama.feature.settings.data.local.UserSettingsEntity
import com.udhay.kollama.feature.settings.data.local.UserSettingsDao
import com.udhay.kollama.feature.tools.data.local.ToolDao
import com.udhay.kollama.feature.tools.data.local.ToolEntity

@Database(
    entities = [
        UserSettingsEntity::class,
        ChatEntity::class,
        ChatMessageEntity::class,
        ToolEntity::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userSettingsDao(): UserSettingsDao

    abstract fun chatDao(): ChatDao

    abstract fun toolDao(): ToolDao

    companion object {
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "kollama.db"
            )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}
