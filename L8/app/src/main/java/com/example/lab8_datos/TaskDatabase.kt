package com.example.lab8_datos


import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Room

import androidx.room.TypeConverters


@Database(entities = [Task::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class) // Asegúrate de que los TypeConverters están configurados
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                )
                    .fallbackToDestructiveMigration() // Permite que Room destruya y recree la BD en cambios de esquema
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}