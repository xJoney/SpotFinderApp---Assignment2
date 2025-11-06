package com.example.Assignment2

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AlertDialog
import android.widget.EditText
import android.widget.Toast



class AddressAdapter(
    private val dbHelper: DatabaseHelper,
    private val addressList: MutableList<AddressData>
) :
    RecyclerView.Adapter<AddressAdapter.ViewHolder>() {
    class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_address, parent, false)) {
        val txtAddress: TextView = itemView.findViewById(R.id.txtAddress)
        val txtLatLng: TextView = itemView.findViewById(R.id.txtLatLng)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    // display info for a single item
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = addressList[position]
        holder.txtAddress.text = item.address
        holder.txtLatLng.text = "Lat: ${item.latitude}, Lng: ${item.longitude}"

        // wait for user to click on item, allows user to delete/update the address
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Manage Address")

            val input = EditText(context)
            input.setText(item.address)
            builder.setView(input)

            // tries to update by calling db.updateAddress with new address user inputs
            builder.setPositiveButton("Update") { dialog, _ ->
                val newAddress = input.text.toString().trim()
                if (newAddress.isNotEmpty()) {
                    val updated = dbHelper.updateAddress(item.id, newAddress)
                    if (updated) {
                        Toast.makeText(context, "Address updated!", Toast.LENGTH_SHORT).show()
                        addressList[position] = item.copy(address = newAddress)
                        notifyItemChanged(position)
                    } else {
                        Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Address cannot be empty", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }



            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            // deletes current item by calling deleteaddress and passing id of item user wants to delete
            builder.setNeutralButton("Delete") { dialog, _ ->
                val deleted = dbHelper.deleteAddress(item.id)
                if (deleted) {
                    Toast.makeText(context, "Address deleted", Toast.LENGTH_SHORT).show()
                    addressList.removeAt(position)
                    notifyItemRemoved(position)
                } else {
                    Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }

            builder.show()
        }
    }
    fun addItem(item: AddressData) {
        addressList.add(item)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int = addressList.size
}
