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
import androidx.compose.material3.Button
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
    // Recoger el estado de las tareas
    val tasks by viewModel.tasks.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var newTaskDescription by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Campo de texto para nueva tarea
        TextField(
            value = newTaskDescription,
            onValueChange = { newTaskDescription = it },
            label = { Text("Nueva tarea") },
            modifier = Modifier.fillMaxWidth()
        )

        // Botón para agregar tarea
        Button(
            onClick = {
                if (newTaskDescription.isNotEmpty()) {
                    viewModel.addTask(newTaskDescription)
                    newTaskDescription = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Agregar tarea")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de tareas
        tasks.forEach { task ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = task.description)
                Button(onClick = { viewModel.toggleTaskCompletion(task) }) {
                    Text(if (task.isCompleted) "Completada" else "Pendiente")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para eliminar todas las tareas
        Button(
            onClick = { coroutineScope.launch { viewModel.deleteAllTasks() } },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Eliminar todas las tareas")
        }
    }
}


