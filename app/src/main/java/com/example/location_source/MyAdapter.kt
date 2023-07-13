package com.example.location_source

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.location_source.databinding.ActivityAddLocationLayoutBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MyAdapter(
    private val context: Context,
    private val arrayList: ArrayList<AddLocationDataClass>
) : BaseAdapter() {

    private lateinit var binding: ActivityAddLocationLayoutBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        binding = ActivityAddLocationLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
//        val distance = AddLocationDataClass(arrayList[position].Location, arrayList[position].Address,arrayList[position].Distance.toString()) //+ " km"
        binding.list=arrayList[position]

        if (arrayList[position].IsPrimary ) {
            binding.tvPrimary.visibility = View.VISIBLE
            binding.tvDistance.visibility = View.GONE
        } else {
            binding.tvPrimary.visibility = View.GONE
            binding.tvDistance.visibility = View.VISIBLE
        }

        sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        binding.tvDeleat.setOnClickListener {
            // Remove item from the ArrayList
            val removedItem = arrayList.removeAt(position)
            notifyDataSetChanged()

            // Delete the corresponding item from SharedPreferences
            deleteItemFromSharedPreferences(removedItem)
        }

        binding.tvEdit.setOnClickListener {
            val intent = Intent(context, MapsActivity::class.java)
            context.startActivity(intent)
        }

        return binding.root
    }

    private fun deleteItemFromSharedPreferences(item: AddLocationDataClass) {
        val gson = Gson()
        val listJson = sharedPreferences.getString("List", null)
        if (!listJson.isNullOrEmpty()) {
            val type = object : TypeToken<ArrayList<AddLocationDataClass>>() {}.type
            val retrievedList = gson.fromJson<ArrayList<AddLocationDataClass>>(listJson, type)
            if (retrievedList != null) {
                retrievedList.remove(item)
                val updatedListJson = gson.toJson(retrievedList)
                val editor = sharedPreferences.edit()
                editor.putString("List", updatedListJson)
                editor.apply()
            }
        }
    }
}




