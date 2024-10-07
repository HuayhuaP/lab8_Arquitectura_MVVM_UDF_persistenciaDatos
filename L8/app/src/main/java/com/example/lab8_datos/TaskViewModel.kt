package com.example.lab8_datos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class TaskViewModel(private val dao: TaskDao) : ViewModel() {

    // Estado para la lista de tareas
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    // Estado para el filtro de tareas
    private val _filter = MutableStateFlow("ALL")
    val filter: StateFlow<String> = _filter

    // Estado para la búsqueda de tareas
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Estado para la ordenación de tareas
    private val _sortOrder = MutableStateFlow("DATE_DESC")
    val sortOrder: StateFlow<String> = _sortOrder

    init {
        viewModelScope.launch {
            loadTasks()
        }
    }

    private suspend fun loadTasks() {
        val currentFilter = _filter.value
        val currentQuery = _searchQuery.value
        val currentSortOrder = _sortOrder.value

        // Obtener tareas según el filtro
        val filteredTasks = when (currentFilter) {
            "COMPLETED" -> dao.getCompletedTasks()
            "PENDING" -> dao.getPendingTasks()
            else -> dao.getAllTasks()
        }

        // Aplicar búsqueda si hay una consulta
        val searchedTasks = if (currentQuery.isNotEmpty()) {
            filteredTasks.filter { it.description.contains(currentQuery, ignoreCase = true) }
        } else {
            filteredTasks
        }

        // Aplicar ordenación
        _tasks.value = when (currentSortOrder) {
            "NAME_ASC" -> searchedTasks.sortedBy { it.description }
            "NAME_DESC" -> searchedTasks.sortedByDescending { it.description }
            "DATE_ASC" -> searchedTasks.sortedBy { it.dateCreated }
            "DATE_DESC" -> searchedTasks.sortedByDescending { it.dateCreated }
            "STATUS" -> searchedTasks.sortedByDescending { it.isCompleted }
            else -> searchedTasks
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

    fun setSortOrder(newSortOrder: String) {
        viewModelScope.launch {
            _sortOrder.value = newSortOrder
            loadTasks()
        }
    }

    // Actualizar para aceptar dueDate
    fun addTask(description: String, dueDate: Long?) {
        val newTask = Task(description = description, dueDate = dueDate)
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

    // Actualizar para aceptar dueDate
    fun editTask(task: Task, newDescription: String, newDueDate: Long?) {
        val updatedTask = task.copy(description = newDescription, dueDate = newDueDate)
        viewModelScope.launch {
            dao.updateTask(updatedTask)
            loadTasks()
        }
    }
}