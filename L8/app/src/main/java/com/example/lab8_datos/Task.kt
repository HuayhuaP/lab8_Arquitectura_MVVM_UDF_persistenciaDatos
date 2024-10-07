package com.example.lab8_datos


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false,
    @ColumnInfo(name = "date_created") val dateCreated: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "priority") val priority: Priority = Priority.MEDIUM,
    @ColumnInfo(name = "due_date") val dueDate: Long? = null // Campo opcional para fecha de vencimiento
)