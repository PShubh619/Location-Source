package com.example.location_source.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.example.location_source.model.AddLocationDataClass
import com.example.location_source.R
import com.example.location_source.databinding.ActivityAddLocationBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AddLocationActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var clListViewEmpty: ConstraintLayout
    private lateinit var addLocation: Button
    private lateinit var addLocationEmpty: Button
    private lateinit var fbtnShowRoute: FloatingActionButton
    private lateinit var ivList: ImageView
    private lateinit var Latitude: String
    private lateinit var Longitude: String
    var adapter: MyAdapter? = null
    var arrayList: ArrayList<AddLocationDataClass> = ArrayList()

    //    private val selectedLocations: ArrayList<String> = ArrayList()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var addActivityResultLauncher: ActivityResultLauncher<Intent>
    var distance: String = ""
    var currentAddress: String = ""
    var location: String = ""
    lateinit var gson: Gson
    lateinit var listJson: String
    lateinit var editor: SharedPreferences
    private lateinit var binding: ActivityAddLocationBinding


    @SuppressLint("MissingInflatedId", "CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_location)
//        setContentView(binding.root)

        listView = binding.lvLocation
        clListViewEmpty = binding.clListviewEmpty
        addLocation = binding.btnAddLocation
        addLocationEmpty = binding.btnAddLocationEmpty
        fbtnShowRoute = binding.fbtnShowRoute
        ivList = binding.ivList


        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

//        for (i in 0..100){
//            print(i)
//        }


        addActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    distance = sharedPreferences.getString("distance", "").toString()
                    currentAddress = sharedPreferences.getString("address", "").toString()
                    location = sharedPreferences.getString("location", "").toString()
                    Latitude = sharedPreferences.getString("Latitude", "").toString()
                    Longitude = sharedPreferences.getString("Longitude", "").toString()

                    // Set IsPrimary property
                    val isPrimary = arrayList.isEmpty() // Check if the list is empty
                    arrayList.add(
                        AddLocationDataClass(
                            location,
                            currentAddress,
                            distance,
                            Latitude,
                            Longitude,
                            IsPrimary = isPrimary
                        )
                    )

                    // Set IsPrimary to false for other items in the list
                    for (i in 1 until arrayList.size) {
                        arrayList[i].IsPrimary = false
                    }
                    gson = Gson()
                    listJson = gson.toJson(arrayList)
                    val editor = sharedPreferences.edit()
                    editor.putString("List", listJson)
                    editor.apply()

                    adapter?.notifyDataSetChanged()
                }
            }

        listJson = sharedPreferences.getString("List", null).toString()
        if (!listJson.isNullOrEmpty()) {
            val gson = Gson()
            val type = object : TypeToken<ArrayList<AddLocationDataClass>>() {}.type
            val retrievedList = gson.fromJson<ArrayList<AddLocationDataClass>>(listJson, type)
            if (retrievedList != null) {
                arrayList.addAll(retrievedList)
                adapter?.notifyDataSetChanged()
            }
        }

//        var bottomSheet = BottomSheet()
        ivList.setOnClickListener {
            val isSelected: String = ""
            BottomSheet.newInstance(isSelected, sortBy = {
                val OrdeOfList = sharedPreferences.getString("OrderOfList", null)
                if (OrdeOfList == "Ascending") {
                    val sort = arrayList.sortedBy { it.Distance }
                    arrayList.clear()
                    arrayList.addAll(sort)
                    adapter?.notifyDataSetChanged()
                } else if (OrdeOfList == "Descending") {
                    val sort = arrayList.sortedByDescending { it.Distance }
                    arrayList.clear()
                    arrayList.addAll(sort)
                    adapter?.notifyDataSetChanged()
                }
            }).show(supportFragmentManager, "TAG")
        }


//        val editor = sharedPreferences.edit()
//        editor.putString("List", arrayList.toString())


        updatevicibility()

//        val stringBuffer= StringBuffer()
//        val retrievedData = sharedPreferences.getString("data", "")

//        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
//        val distance = sharedPreferences.getString("distance", "")
//        val currentAddress = sharedPreferences.getString("address", "")
//        val location = sharedPreferences.getString("location", "")

//        if (currentAddress != null && !distance.isNullOrEmpty()) {
//            try {
//                val distanceValue = distance.toDouble()
//
//
//            } catch (e: NumberFormatException) {
//                // Handle the case where distance is not a valid numeric value
//                // You can log an error, show a message, or take appropriate action
//            }
//        }
        addLocation.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            addActivityResultLauncher.launch(intent)

        }
        addLocationEmpty.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            addActivityResultLauncher.launch(intent)
        }

        fbtnShowRoute.setOnClickListener {
            val intent = Intent(this, MapsRoutingActivity::class.java)
            addActivityResultLauncher.launch(intent)

        }

        adapter = MyAdapter(this, arrayList)
        listView.adapter = adapter


    }


    override fun onResume() {
        super.onResume()
        updatevicibility()
    }

    private fun updatevicibility() {
        if (arrayList.isEmpty()) {
            clListViewEmpty.visibility = View.VISIBLE
            addLocation.visibility = View.GONE
            fbtnShowRoute.visibility = View.GONE
        } else {
            clListViewEmpty.visibility = View.GONE
            addLocation.visibility = View.VISIBLE
            listView.visibility = View.VISIBLE
            fbtnShowRoute.visibility = View.VISIBLE
//            sortLocationsByDistance()
//            updateListView()
        }
    }

//    private fun sortLocationsByDistance() {
//
//        val OrdeOfList = sharedPreferences.getString("AscendingOrder", "").toBoolean()
//        if (OrdeOfList==true){
//           val sort = arrayList.sortedBy { it.Distance }
//            arrayList.clear()
//            arrayList.addAll(sort)
//            adapter?.notifyDataSetChanged()
//        }
//        if (OrdeOfList==false){
//            val sort = arrayList.sortedByDescending { it.Distance }
//            arrayList.clear()
//            arrayList.addAll(sort)
//            adapter?.notifyDataSetChanged()
//        }
//
//    }


//        private fun saveList() {
//        val gson = Gson()
//        val listJson = gson.toJson(arrayList)
//        val editor = sharedPreferences.edit()
//        editor.putString("List", listJson)
//        editor.apply()
//    }


//    private fun updateListView() {
//        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, selectedLocations)
//        listView.adapter = adapter
//    }

}


