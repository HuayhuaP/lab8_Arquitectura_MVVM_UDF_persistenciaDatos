package com.example.lab8_datos

import androidx.room.*

@Dao
interface TaskDao {

    // Obtener todas las tareas
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>

    // Obtener tareas completadas
    @Query("SELECT * FROM tasks WHERE is_completed = 1")
    suspend fun getCompletedTasks(): List<Task>

    // Obtener tareas pendientes
    @Query("SELECT * FROM tasks WHERE is_completed = 0")
    suspend fun getPendingTasks(): List<Task>

    // Buscar tareas por descripción
    @Query("SELECT * FROM tasks WHERE description LIKE '%' || :query || '%'")
    suspend fun searchTasks(query: String): List<Task>

    // Insertar una nueva tarea
    @Insert
    suspend fun insertTask(task: Task)

    // Actualizar una tarea existente
    @Update
    suspend fun updateTask(task: Task)

    // Eliminar una tarea específica
    @Delete
    suspend fun deleteTask(task: Task)

    // Eliminar todas las tareas
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    // Obtener tareas ordenadas por nombre ascendente
    @Query("SELECT * FROM tasks ORDER BY description ASC")
    suspend fun getTasksOrderByNameAsc(): List<Task>

    // Obtener tareas ordenadas por nombre descendente
    @Query("SELECT * FROM tasks ORDER BY description DESC")
    suspend fun getTasksOrderByNameDesc(): List<Task>

    // Obtener tareas ordenadas por fecha de creación ascendente
    @Query("SELECT * FROM tasks ORDER BY date_created ASC")
    suspend fun getTasksOrderByDateAsc(): List<Task>

    // Obtener tareas ordenadas por fecha de creación descendente
    @Query("SELECT * FROM tasks ORDER BY date_created DESC")
    suspend fun getTasksOrderByDateDesc(): List<Task>

    // Obtener tareas ordenadas por estado (completada primero)
    @Query("SELECT * FROM tasks ORDER BY is_completed DESC")
    suspend fun getTasksOrderByStatus(): List<Task>
}
