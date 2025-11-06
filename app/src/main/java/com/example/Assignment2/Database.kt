package com.example.Assignment2

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val DATABASE_NAME = "MyDatabase.db"
            private const val DATABASE_VERSION = 1
            const val TABLE_NAME = "AddressBook"
            const val COLUMN_ID = "id"
            const val COLUMN_ADDRESS = "address"
            const val COLUMN_LONGITUDE = "longitude"
            const val COLUMN_LATITUDE = "latitude"
        }

        override fun onCreate(db: SQLiteDatabase) {
            val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ADDRESS TEXT,
                $COLUMN_LONGITUDE TEXT,
                $COLUMN_LATITUDE TEXT
            )
        """.trimIndent()
            db.execSQL(createTable)
        }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }


    // create function, inputs a new address into db
    fun insertAddress(address: String, longitude: String, latitude: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_ADDRESS, address)
            put(COLUMN_LONGITUDE,longitude)
            put(COLUMN_LATITUDE,latitude)
        }
        val result = db.insert(TABLE_NAME,null,values)
        db.close()
        return result
    }

    // read function, reads the rows from the Address table
    fun readAddressBook(): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    // delete function, finds the address and deletes the row from db
    fun deleteAddress(id: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

    // update function, finds the old address, and updates the name with the new address
    fun updateAddress(id: Int,newAddress: String): Boolean{
        val db = writableDatabase
        val values = ContentValues().apply{
            put(COLUMN_ADDRESS, newAddress)
        }
        val result = db.update(TABLE_NAME,values,"$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }

}