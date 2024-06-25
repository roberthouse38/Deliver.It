package com.example.deliverit

import Item
import Note
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 3
        private const val TABLE_NAME = "data"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "Username"
        private const val COLUMN_PASSWORD = "Password"

        private const val TABLE_ITEMS = "items"
        private const val COLUMN_ITEM_ID = "item_id"
        private const val COLUMN_ITEM_NAME = "name"
        private const val COLUMN_ITEM_QUANTITY = "quantity"
        private const val COLUMN_ITEM_DESCRIPTION = "description"

        private const val TABLE_NOTES = "notes"
        private const val COLUMN_NOTE_ID = "note_id"
        private const val COLUMN_NOTE_TITLE = "title"
        private const val COLUMN_NOTE_CONTENT = "content"
        private const val COLUMN_NOTE_IMAGE = "image" // Path to image or blob

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTabQuery = ("CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USERNAME TEXT, " +
                "$COLUMN_PASSWORD TEXT)")
        db?.execSQL(createTabQuery)

        val createItemsTableQuery = ("CREATE TABLE $TABLE_ITEMS (" +
                "$COLUMN_ITEM_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_ITEM_NAME TEXT, " +
                "$COLUMN_ITEM_QUANTITY INTEGER, " +
                "$COLUMN_ITEM_DESCRIPTION TEXT)")
        db?.execSQL(createItemsTableQuery)

        val createNotesTableQuery = ("CREATE TABLE $TABLE_NOTES (" +
                "$COLUMN_NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NOTE_TITLE TEXT, " +
                "$COLUMN_NOTE_CONTENT TEXT, " +
                "$COLUMN_NOTE_IMAGE BLOB)")
        db?.execSQL(createNotesTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop tabel jika sudah ada versi sebelumnya

        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)

        val dropItemsTableQuery = "DROP TABLE IF EXISTS $TABLE_ITEMS"
        db?.execSQL(dropItemsTableQuery)

        val dropNotesTableQuery = "DROP TABLE IF EXISTS $TABLE_NOTES"
        db?.execSQL(dropNotesTableQuery)

        onCreate(db)
    }

    fun insertUser(username: String, password: String): Long {
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
        }

        val db = writableDatabase
        return db.insert(TABLE_NAME, null, values)
    }

    fun readUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val selection = "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(username, password)
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)

        val userExist = cursor.count > 0
        cursor.close()
        return userExist
    }

    fun insertItem(itemName: String, quantity: Int, description: String): Long {
        val values = ContentValues().apply {
            put(COLUMN_ITEM_NAME, itemName)
            put(COLUMN_ITEM_QUANTITY, quantity)
            put(COLUMN_ITEM_DESCRIPTION, description)
        }
        val db = writableDatabase
        return db.insert(TABLE_ITEMS, null, values)
    }

    fun updateItem(itemId: Long, itemName: String, quantity: Int, description: String): Int {
        val values = ContentValues().apply {
            put(COLUMN_ITEM_NAME, itemName)
            put(COLUMN_ITEM_QUANTITY, quantity)
            put(COLUMN_ITEM_DESCRIPTION, description)
        }
        val db = writableDatabase
        return db.update(TABLE_ITEMS, values, "$COLUMN_ITEM_ID=?", arrayOf(itemId.toString()))
    }

    fun deleteItem(itemId: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_ITEMS, "$COLUMN_ITEM_ID=?", arrayOf(itemId.toString()))
    }

    @SuppressLint("Range")
    fun getAllItems(): List<Item> {
        val itemsList = mutableListOf<Item>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_ITEMS"
        val cursor = db.rawQuery(query, null)

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val itemId = it.getLong(it.getColumnIndex(COLUMN_ITEM_ID))
                    val itemName = it.getString(it.getColumnIndex(COLUMN_ITEM_NAME))
                    val quantity = it.getInt(it.getColumnIndex(COLUMN_ITEM_QUANTITY))
                    val description = it.getString(it.getColumnIndex(COLUMN_ITEM_DESCRIPTION))
                    val item = Item(itemId, itemName, quantity, description)
                    itemsList.add(item)
                } while (it.moveToNext())
            }
        }
        cursor.close()
        return itemsList
    }

    @SuppressLint("Range")
    fun getItemById(itemId: Long): Item? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_ITEMS WHERE $COLUMN_ITEM_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(itemId.toString()))

        var item: Item? = null
        cursor?.use {
            if (it.moveToFirst()) {
                val itemName = it.getString(it.getColumnIndex(COLUMN_ITEM_NAME))
                val quantity = it.getInt(it.getColumnIndex(COLUMN_ITEM_QUANTITY))
                val description = it.getString(it.getColumnIndex(COLUMN_ITEM_DESCRIPTION))
                item = Item(itemId, itemName, quantity, description)
            }
        }

        cursor.close()
        return item
    }

    fun decreaseItemQuantity(itemId: Long):Int{
        val item = getItemById(itemId)
        if (item != null && item.quantity > 0) {
            val newQuantity = item.quantity - 1
            val values = ContentValues().apply {
                put(COLUMN_ITEM_QUANTITY, newQuantity)
            }
            val db = writableDatabase
            return db.update(TABLE_ITEMS, values, "$COLUMN_ITEM_ID=?", arrayOf(itemId.toString()))
        }
        return 0
    }

    fun insertNote(title: String, content: String, image: ByteArray?): Long {
        val values = ContentValues().apply {
            put(COLUMN_NOTE_TITLE, title)
            put(COLUMN_NOTE_CONTENT, content)
            put(COLUMN_NOTE_IMAGE, image)
        }
        val db = writableDatabase
        return db.insert(TABLE_NOTES, null, values)
    }

    @SuppressLint("Range")
    fun getNoteById(noteId: Long): Note? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NOTES WHERE $COLUMN_NOTE_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(noteId.toString()))

        var note: Note? = null
        cursor?.use {
            if (it.moveToFirst()) {
                val title = it.getString(it.getColumnIndex(COLUMN_NOTE_TITLE))
                val content = it.getString(it.getColumnIndex(COLUMN_NOTE_CONTENT))
                val image = it.getBlob(it.getColumnIndex(COLUMN_NOTE_IMAGE))
                note = Note(noteId, title, content, image)
            }
        }

        cursor.close()
        return note
    }

    @SuppressLint("Range")
    fun getAllNotes(): List<Note> {
        val notesList = mutableListOf<Note>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NOTES"
        val cursor = db.rawQuery(query, null)

        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val noteId = it.getLong(it.getColumnIndex(COLUMN_NOTE_ID))
                    val title = it.getString(it.getColumnIndex(COLUMN_NOTE_TITLE))
                    val content = it.getString(it.getColumnIndex(COLUMN_NOTE_CONTENT))
                    val image = it.getBlob(it.getColumnIndex(COLUMN_NOTE_IMAGE))
                    val note = Note(noteId, title, content, image)
                    notesList.add(note)
                } while (it.moveToNext())
            }
        }
        cursor.close()
        return notesList
    }

    fun updateNote(noteId: Long, title: String, content: String, image: ByteArray?): Int {
        val values = ContentValues().apply {
            put(COLUMN_NOTE_TITLE, title)
            put(COLUMN_NOTE_CONTENT, content)
            put(COLUMN_NOTE_IMAGE, image)
        }
        val db = writableDatabase
        return db.update(TABLE_NOTES, values, "$COLUMN_NOTE_ID=?", arrayOf(noteId.toString()))
    }

    fun deleteNote(noteId: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_NOTES, "$COLUMN_NOTE_ID=?", arrayOf(noteId.toString()))
    }

}

