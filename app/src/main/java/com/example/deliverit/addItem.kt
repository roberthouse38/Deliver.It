package com.example.deliverit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class addItem : ComponentActivity() {

    private val dbHelper by lazy { DbHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddItemScreen()
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun AddItemScreen() {
        var itemName by remember { mutableStateOf("") }
        var quantity by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var message by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Add New Item", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Item Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val quantityInt = quantity.toIntOrNull()
                    if (itemName.isNotBlank() && quantityInt != null && description.isNotBlank()) {
                        val result = dbHelper.insertItem(itemName, quantityInt, description)
                        message = if (result != -1L) {
                            "Item added successfully!"
                        } else {
                            "Failed to add item."
                        }
                    } else {
                        message = "Please fill in all fields with valid data."
                    }
                }
            ) {
                Text("Save Item")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
