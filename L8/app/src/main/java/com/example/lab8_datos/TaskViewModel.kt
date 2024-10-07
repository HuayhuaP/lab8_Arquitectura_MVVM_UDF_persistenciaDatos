package com.example.lab8_datos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



class TaskViewModel(private val dao: TaskDao) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _filter = MutableStateFlow("ALL")
    val filter: StateFlow<String> = _filter

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery


    init {
        viewModelScope.launch {
            loadTasks()
        }
    }

    private suspend fun loadTasks() {
        _tasks.value = when (_filter.value) {
            "COMPLETED" -> dao.getCompletedTasks()
            "PENDING" -> dao.getPendingTasks()
            else -> dao.getAllTasks()
        }
    }

    fun setFilter(newFilter: String) {
        viewModelScope.launch {
            _filter.value = newFilter
            loadTasks()
        }
    }



    fun setSearchQuery(query: String) {
        viewModelScope.launch {
            _searchQuery.value = query
            loadTasks()
        }
    }








    fun addTask(description: String) {
        val newTask = Task(description = description)
        viewModelScope.launch {
            dao.insertTask(newTask)
            loadTasks()
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = !task.isCompleted)
            dao.updateTask(updatedTask)
            loadTasks()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.deleteTask(task)
            loadTasks()
        }
    }

    fun deleteAllTasks() {
        viewModelScope.launch {
            dao.deleteAllTasks()
            _tasks.value = emptyList()
        }
    }

    // Nueva función para editar una tarea
    fun editTask(task: Task, newDescription: String) {
        val updatedTask = task.copy(description = newDescription)
        viewModelScope.launch {
            dao.updateTask(updatedTask)
            loadTasks()
        }
    }
}