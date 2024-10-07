package com.example.lab8_datos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.lab8_datos.ui.theme.Lab8_datosTheme
import kotlinx.coroutines.launch


import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color


import androidx.compose.foundation.background

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.window.Dialog

import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search


import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.text.input.TextFieldValue


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab8_datosTheme {
                // Crear la instancia de la base de datos
                val db = Room.databaseBuilder(
                    applicationContext,
                    TaskDatabase::class.java,
                    "task_db"
                ).build()

                // Obtener el DAO
                val taskDao = db.taskDao()

                // Crear el ViewModel
                val viewModel = TaskViewModel(taskDao)

                // Mostrar la pantalla principal
                TaskScreen(viewModel)
            }
        }
    }
}

@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val filter by viewModel.filter.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    var newTaskDescription by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF2F2F2))
    ) {
        // Título de la Aplicación
        Text(
            text = "Tareas",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Campo de texto para nueva tarea
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = newTaskDescription,
                onValueChange = { newTaskDescription = it },
                label = { Text("Agregar una nueva tarea") },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Botón para agregar tarea
            Button(
                onClick = {
                    if (newTaskDescription.isNotEmpty()) {
                        viewModel.addTask(newTaskDescription)
                        newTaskDescription = ""
                    }
                },
                modifier = Modifier
                    .height(56.dp)
            ) {
                Text("Agregar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        // Barra de búsqueda
        TextField(
            value = searchQuery,
            onValueChange = { query ->
                viewModel.setSearchQuery(query)
            },
            label = { Text("Buscar tareas") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))





        // Filtro de tareas
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Filtrar:")
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filtrar tareas"
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Todas") },
                    onClick = {
                        viewModel.setFilter("ALL")
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Completadas") },
                    onClick = {
                        viewModel.setFilter("COMPLETED")
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Pendientes") },
                    onClick = {
                        viewModel.setFilter("PENDING")
                        expanded = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de tareas usando LazyColumn
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onToggleCompletion = { viewModel.toggleTaskCompletion(task) },
                    onDeleteTask = { viewModel.deleteTask(task) },
                    onEditTask = { newDescription -> viewModel.editTask(task, newDescription) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para eliminar todas las tareas
        Button(
            onClick = { coroutineScope.launch { viewModel.deleteAllTasks() } },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Eliminar todas las tareas")
        }
    }
}


@Composable
fun TaskItem(
    task: Task,
    onToggleCompletion: () -> Unit,
    onDeleteTask: () -> Unit,
    onEditTask: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editedDescription by remember { mutableStateOf(task.description) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Descripción de la tarea
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            // Botón para alternar estado de completado
            Button(
                onClick = onToggleCompletion,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (task.isCompleted) Color(0xFF4CAF50) else Color(0xFFF44336)
                ),
                modifier = Modifier
                    .height(36.dp)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = if (task.isCompleted) "Completada" else "Pendiente",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Botón para editar tarea
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar tarea",
                    tint = Color.Blue
                )
            }

            // Botón para eliminar tarea
            IconButton(onClick = onDeleteTask) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar tarea",
                    tint = Color.Red
                )
            }
        }

        // Diálogo para editar tarea
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Editar Tarea") },
                text = {
                    TextField(
                        value = editedDescription,
                        onValueChange = { editedDescription = it },
                        label = { Text("Descripción") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (editedDescription.isNotEmpty()) {
                                onEditTask(editedDescription)
                            }
                            showDialog = false
                        }
                    ) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

