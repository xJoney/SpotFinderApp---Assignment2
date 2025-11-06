package com.example.Assignment2

import android.database.Cursor
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AlertDialog
import android.widget.EditText
import android.widget.Toast
import android.location.Geocoder
import java.util.Locale
import android.content.ContentValues
import androidx.core.database.getIntOrNull


class AddressList : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: AddressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.address_list)

        recyclerView = findViewById(R.id.recyclerViewAddresses)
        recyclerView.layoutManager = LinearLayoutManager(this)
        dbHelper = DatabaseHelper(this)


        // recycler view to display list of addresses
        val addresses = getAddressesFromDB().toMutableList()
        adapter = AddressAdapter(dbHelper,addresses)
        recyclerView.adapter = adapter


        //back button to go back to home page
        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        // add button that will create a dialogue so user can insert new address into db
        val btnAdd = findViewById<Button>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Add Address")
            val input = EditText(this)
            input.hint = "Enter address here"
            builder.setView(input)


            builder.setPositiveButton("Add") { dialog, _ ->
                val address = input.text.toString()
                val geocoder = Geocoder(this, Locale.getDefault())
                val results = geocoder.getFromLocationName(address, 1)

                if (!results.isNullOrEmpty()) {
                    val location = results[0]
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val rowId = dbHelper.insertAddress(address, longitude.toString(), latitude.toString())
                    if (rowId != -1L) {
                        Toast.makeText(this, "Address added to DB", Toast.LENGTH_SHORT).show()
                        adapter.addItem(AddressData(rowId.toInt(), address, longitude.toString(), latitude.toString()))
                    } else {
                        Toast.makeText(this, "Insert failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Address doesn't exist", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }

            builder.setNegativeButton("Back"){ dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }
    }



    // function to get address by using cursor to iterate through the db
    private fun getAddressesFromDB(): List<AddressData> {
        val cursor: Cursor = dbHelper.readAddressBook()
        val addressList = mutableListOf<AddressData>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val address = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADDRESS))
                val longitude = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LONGITUDE))
                val latitude = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LATITUDE))
                addressList.add(AddressData(id,address, longitude, latitude))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return addressList
    }


}
