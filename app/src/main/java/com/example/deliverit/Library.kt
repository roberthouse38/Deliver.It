package com.example.deliverit

import Item
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType

@Suppress("PreviewAnnotationInFunctionWithParameters")
class Library : ComponentActivity() {

    companion object {
        const val EXTRA_USERNAME = "extra_username"
    }

    private val dbHelper by lazy { DbHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val username = intent.getStringExtra(EXTRA_USERNAME) ?: ""
        dbHelper.writableDatabase
        setContent {
            libraryPage(username)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun libraryPage(username: String) {
        var items by remember { mutableStateOf(emptyList<Item>()) }
        var selectedItem by remember { mutableStateOf<Item?>(null) }

        LaunchedEffect(Unit) {
            items = dbHelper.getAllItems()
        }

        Scaffold(bottomBar = { BottomNavigationBar(username) }) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(text = "Please Edit the Stock As You Wish, $username")

                Text("Inventory Items:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))

                // Menampilkan daftar item menggunakan LazyColumn
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(items) { item ->
                        ItemRow(item, onEditClicked = { selectedItem = it }, onDeleteClicked = {
                            dbHelper.deleteItem(it.id)
                            items = dbHelper.getAllItems()
                        }, onDecreaseQuantityClicked = {
                            dbHelper.decreaseItemQuantity(it.id)
                            items = dbHelper.getAllItems()
                        })
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // Tombol tambah item
                AddItemButton()
            }

            selectedItem?.let { item ->
                ItemEditDialog(
                    item = item,
                    onDismiss = { selectedItem = null },
                    onUpdate = { name, quantity, description ->
                        dbHelper.updateItem(item.id, name, quantity, description)
                        items = dbHelper.getAllItems()
                        selectedItem = null
                    }
                )
            }
        }
    }

    @Composable
    fun ItemRow(item: Item, onEditClicked: (Item) -> Unit, onDeleteClicked: (Item) -> Unit, onDecreaseQuantityClicked: (Item) -> Unit) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(text = "${item.name}: ${item.quantity}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onEditClicked(item) }) {
                Icon(
                    painter = painterResource(id = R.drawable.deliverlogo),
                    contentDescription = "Edit Item"
                )
            }
            IconButton(onClick = { onDeleteClicked(item) }) {
                Icon(
                    painter = painterResource(id = R.drawable.delete),
                    contentDescription = "Delete Item"
                )
            }
            IconButton(onClick = { onDecreaseQuantityClicked(item) }) {
                Icon(
                    painter = painterResource(id = R.drawable.sell),
                    contentDescription = "Decrease Quantity"
                )
            }
        }
    }

    @Composable
    fun ItemEditDialog(
        item: Item,
        onDismiss: () -> Unit,
        onUpdate: (String, Int, String) -> Unit
    ) {
        var itemName by remember { mutableStateOf(item.name) }
        var itemQuantity by remember { mutableStateOf(item.quantity.toString()) }
        var itemDescription by remember { mutableStateOf(item.description) }

        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        onUpdate(itemName, itemQuantity.toIntOrNull() ?: 0, itemDescription)
                        onDismiss()
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text("Item Name") }
                    )
                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = { itemQuantity = it },
                        label = { Text("Item Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = itemDescription,
                        onValueChange = { itemDescription = it },
                        label = { Text("Item Description") }
                    )
                }
            }
        )
    }

    @Composable
    fun AddItemButton() {
        Button(
            onClick = {
                val intent = Intent(this, addItem::class.java)
                startActivity(intent)
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Add Item")
        }
    }

    @Composable
    fun BottomNavigationBar(username: String) {
        NavigationBar(modifier = Modifier) {
            NavigationBarItem(
                selected = false,
                onClick = {
                    val intent = Intent(this@Library, test::class.java).apply {
                        putExtra(EXTRA_USERNAME, username)
                    }
                    startActivity(intent)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = com.example.deliverit.R.drawable.home),
                        contentDescription = null
                    )
                },
                label = { Text(text = "Home") }
            )

            NavigationBarItem(
                selected = true,
                onClick = { },
                icon = {
                    Icon(
                        painter = painterResource(id = com.example.deliverit.R.drawable.library),
                        contentDescription = null
                    )
                },
                label = { Text(text = "Stock") }
            )

            NavigationBarItem(
                selected = false,
                onClick = {
                    val intent = Intent(this@Library, notes::class.java)
                    startActivity(intent)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_notes_24),
                        contentDescription = null
                    )
                }, label = { Text(text = "Notes") }
            )
        }
    }
}
