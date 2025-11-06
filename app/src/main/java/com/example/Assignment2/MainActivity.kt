package com.example.Assignment2
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.widget.TextView
import android.content.Intent


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var etAddress: EditText
    private lateinit var btnSearch: Button
    private lateinit var tvLatLng: TextView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etAddress = findViewById(R.id.etAddress)
        btnSearch = findViewById(R.id.btnSearch)
        tvLatLng = findViewById(R.id.tvLatLng)
        dbHelper = DatabaseHelper(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // search for address button, calls searchAddressInDatabase function
        btnSearch.setOnClickListener {
            val addressText = etAddress.text.toString().trim()
            if (addressText.isNotEmpty()) {
                searchAddressInDatabase(addressText)
            } else {
                Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show()
            }
        }

        val addressList = findViewById<Button>(R.id.btnAddrList)
        addressList.setOnClickListener(){
            val intent = Intent(this, AddressList::class.java)
            startActivity(intent)
        }
    }

    // enables maps service, set default location to Ontario Tech University
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val tru = LatLng(43.94580, -78.89672)
        mMap.addMarker(MarkerOptions().position(tru).title("Ontario Tech University"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tru, 14f))
    }


    // searches address by iterating through db until address is found
    // if found, mark it on the map and output the lat and long values on screen
    private fun searchAddressInDatabase(address: String) {
        val cursor = dbHelper.readAddressBook()
        var found = false
        var latitude = ""
        var longitude = ""

        if (cursor.moveToFirst()) {
            do {
                val dbAddress = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADDRESS))
                if (dbAddress.equals(address, ignoreCase = true)) {
                    latitude = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LATITUDE))
                    longitude = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LONGITUDE))
                    found = true
                    break
                }
            } while (cursor.moveToNext())
        }
        cursor.close()

        if (found) {
            val latLng = LatLng(latitude.toDouble(), longitude.toDouble())
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title(address))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            tvLatLng.text = "Latitude: ${latLng.latitude}, Longitude: ${latLng.longitude}"
        } else {
            tvLatLng.text = "Address not found in database"
        }
    }

}
