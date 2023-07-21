package com.example.location_source.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.location_source.R
import com.example.location_source.model.AddLocationDataClass
import com.example.location_source.viewmodel.AddLocationViewModel

class MyAdapter(
    private val context: Context,
    private val locationList: ArrayList<AddLocationDataClass>,
    private val viewModel: AddLocationViewModel
) : BaseAdapter() {

    override fun getCount(): Int {
        return locationList.size
    }

    override fun getItem(position: Int): Any {
        return locationList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.activity_add_location_layout, parent, false)

        val location = locationList[position]

        val locationNameTextView: TextView = view.findViewById(R.id.tvLocation)
        val addressTextView: TextView = view.findViewById(R.id.tvAddress)
        val distanceTextView: TextView = view.findViewById(R.id.tvDistance)
        val deleteButton: ImageView = view.findViewById(R.id.tvDeleat)
//            val editButton: ImageView = view.findViewById(R.id.tvEdit)
        val isprimary: TextView = view.findViewById(R.id.tvPrimary)

        locationNameTextView.text = location.location
        addressTextView.text = location.address
        distanceTextView.text = location.distance

        deleteButton.setOnClickListener {
            viewModel.deleteLocation(location)
        }
//            editButton.setOnClickListener {
//                viewModel.updateLocation(location)
//            }

        if (location.distance=="0.0"){
            isprimary.visibility = View.VISIBLE
            distanceTextView.visibility = View.GONE
        }
        else{
            isprimary.visibility = View.GONE
            distanceTextView.visibility = View.VISIBLE
        }


        return view
    }
}








