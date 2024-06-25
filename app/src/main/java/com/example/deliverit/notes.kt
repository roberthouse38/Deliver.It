@file:Suppress("PreviewAnnotationInFunctionWithParameters")

package com.example.deliverit

import Note
import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.ComponentActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deliverit.Library.Companion.EXTRA_USERNAME

class notes : ComponentActivity() {

    private lateinit var dbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DbHelper(this)
        setContent {
            val username = intent.getStringExtra(EXTRA_USERNAME) ?: ""
            notepage(dbHelper, username)
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun notepage(dbHelper: DbHelper, username: String) {
        var notes by remember { mutableStateOf(emptyList<Note>()) }
        var selectedNote by remember { mutableStateOf<Note?>(null) }
        var showAddNoteDialog by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            notes = dbHelper.getAllNotes()
        }

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { showAddNoteDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Note")
                }
            },
            bottomBar = { BottomNavigationBar(username) }
        ) {
            LazyColumn {
                items(notes) { note ->
                    NoteRow(note, onEditClicked = { selectedNote = it }, onDeleteClicked = {
                        dbHelper.deleteNote(it.id)
                        notes = dbHelper.getAllNotes()
                    })
                }
            }

            selectedNote?.let { note ->
                NoteEditDialog(
                    note = note,
                    onDismiss = { selectedNote = null },
                    onUpdate = { title, content, image ->
                        dbHelper.updateNote(note.id, title, content, image)
                        notes = dbHelper.getAllNotes()
                        selectedNote = null
                    }
                )
            }

            if (showAddNoteDialog) {
                AddNoteDialog(
                    onDismiss = { showAddNoteDialog = false },
                    onAdd = { title, content, image ->
                        dbHelper.insertNote(title, content, image)
                        notes = dbHelper.getAllNotes()
                        showAddNoteDialog = false
                    }
                )
            }
        }
    }

    @Composable
    fun NoteRow(note: Note, onEditClicked: (Note) -> Unit, onDeleteClicked: (Note) -> Unit) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = note.title, style = MaterialTheme.typography.headlineLarge)
                Text(text = note.content, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = { onEditClicked(note) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Note")
            }
            IconButton(onClick = { onDeleteClicked(note) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Note")
            }
        }
    }

    @Composable
    fun NoteEditDialog(
        note: Note,
        onDismiss: () -> Unit,
        onUpdate: (String, String, ByteArray?) -> Unit
    ) {
        var title by remember { mutableStateOf(note.title) }
        var content by remember { mutableStateOf(note.content) }
        var image: ByteArray? = null

        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        onUpdate(title, content, image)
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
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") }
                    )
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Content") }
                    )
                    // Add Image picker if needed
                }
            }
        )
    }

    @Composable
    fun AddNoteDialog(
        onDismiss: () -> Unit,
        onAdd: (String, String, ByteArray?) -> Unit
    ) {
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var image: ByteArray? = null

        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        onAdd(title, content, image)
                        onDismiss()
                    }
                ) {
                    Text("Add")
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
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") }
                    )
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Content") }
                    )
                    // Add Image picker if needed
                }
            }
        )
    }

    @Composable
    fun BottomNavigationBar(username: String) {
        NavigationBar(modifier = Modifier) {
            NavigationBarItem(
                selected = false,
                onClick = {
                    val intent = Intent(this@notes, MainActivity::class.java)
                    startActivity(intent)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = com.example.deliverit.R.drawable.home),
                        contentDescription = null
                    )
                }, label = { Text(text = "Home") }
            )

            NavigationBarItem(
                selected = false,
                onClick = {
                    val intent = Intent(this@notes, Library::class.java).apply {
                        putExtra(EXTRA_USERNAME, username)
                    }
                    startActivity(intent)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = com.example.deliverit.R.drawable.library),
                        contentDescription = null
                    )
                }, label = { Text(text = "Stock") }
            )

            NavigationBarItem(
                selected = true,
                onClick = { },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_notes_24),
                        contentDescription = null
                    )
                }, label = { Text(text = "Notes") }
            )
        }
    }

    private fun navigateToPage(java: Class<*>) {
        val intent = Intent(this, java)
        startActivity(intent)
    }
}
