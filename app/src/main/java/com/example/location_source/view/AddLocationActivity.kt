package com.example.location_source.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.location_source.DataBaseInstance
import com.example.location_source.R
import com.example.location_source.databinding.ActivityAddLocationBinding
import com.example.location_source.databinding.ActivityAddLocationLayoutBinding
import com.example.location_source.model.AddLocationDataClass
import com.example.location_source.viewmodel.AddLocationViewModel
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
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var addActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var binding: ActivityAddLocationBinding
    private lateinit var adapter: MyAdapter
    private var arrayList: ArrayList<AddLocationDataClass> = ArrayList()
    private  var orderOfList :String = "Ascending"

    // Declare ViewModel
    private lateinit var viewModel: AddLocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_location)

        listView = binding.lvLocation
        clListViewEmpty = binding.clListviewEmpty
        addLocation = binding.btnAddLocation
        addLocationEmpty = binding.btnAddLocationEmpty
        fbtnShowRoute = binding.fbtnShowRoute
        ivList = binding.ivList

        val db = (application as DataBaseInstance).db

        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)



        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(AddLocationViewModel::class.java)


        viewModel.ascLocationList.observe(this) { locationList ->
//            Log.d("Result", locationList.toString())
            arrayList.clear()
            arrayList.addAll(locationList)
            adapter.notifyDataSetChanged()
            updateVisibility()
            sortLocationsByDistance()

        }


        addActivityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    updateVisibility()
//                    val gson = Gson()
//                    val listJson = gson.toJson(arrayList)
//                    val editor = sharedPreferences.edit()
//                    editor.putString("List", listJson)
//                    editor.apply()
                }

            }

        adapter = MyAdapter(this, arrayList, viewModel)
        listView.adapter = adapter

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

        ivList.setOnClickListener {
            openBottomSheet()
        }   
        updateVisibility()
    }

    private fun updateVisibility() {
        if (arrayList.isEmpty()) {
            clListViewEmpty.visibility = View.VISIBLE
            addLocation.visibility = View.GONE
            fbtnShowRoute.visibility = View.GONE
        } else {
            clListViewEmpty.visibility = View.GONE
            addLocation.visibility = View.VISIBLE
            listView.visibility = View.VISIBLE
            fbtnShowRoute.visibility = View.VISIBLE
        }
    }

    private fun openBottomSheet() {
        val bottomSheet = BottomSheet.newInstance("Ascending") {
            orderOfList = sharedPreferences.getString("OrderOfList", null).toString()
            sortLocationsByDistance()        }
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }


    private fun sortLocationsByDistance() {
        if (orderOfList=="Descending"){
//            Log.e("BeforeSort",arrayList.toString())
            val sort = arrayList.sortedByDescending { it.distance.toDouble() }
            arrayList.clear()
            arrayList.addAll(sort)
//            Log.e("Sort",arrayList.toString())
            adapter.notifyDataSetChanged()
        }else{
            val sort = arrayList.sortedBy { it.distance.toDouble() }
            arrayList.clear()
            arrayList.addAll(sort)
            adapter.notifyDataSetChanged()
        }

    }
//    fun primary(){
//        if ()
//    }
}


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




