package com.example.lab8_datos

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import java.util.concurrent.TimeUnit

class TaskReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    companion object {
        const val CHANNEL_ID = "TASK_REMINDER_CHANNEL"
        const val NOTIFICATION_ID = 1

        fun createTaskReminderWork(task: Task): OneTimeWorkRequest {
            val delay = task.dueDate?.let {
                val currentTime = System.currentTimeMillis()
                val diff = it - currentTime
                if (diff > 0) diff else 0
            } ?: 0L

            return OneTimeWorkRequestBuilder<TaskReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf(
                    "TASK_ID" to task.id,
                    "TASK_DESCRIPTION" to task.description
                ))
                .build()
        }
    }

    override fun doWork(): Result {
        val taskId = inputData.getInt("TASK_ID", -1)
        val taskDescription = inputData.getString("TASK_DESCRIPTION") ?: "Tarea sin descripción"

        if (taskId == -1) {
            return Result.failure()
        }

        showNotification(taskDescription)

        return Result.success()
    }

    private fun showNotification(taskDescription: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear el canal de notificación si es necesario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Recordatorios de Tareas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para recordatorios de tareas pendientes"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Asegúrate de tener este ícono en tu carpeta de recursos
            .setContentTitle("Recordatorio de Tarea")
            .setContentText("Es hora de completar: $taskDescription")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(taskId, notification)
    }
}

