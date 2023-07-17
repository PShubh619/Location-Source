package com.example.location_source.viewmodel

import android.content.Context
import android.content.Intent
import androidx.databinding.BaseObservable
import androidx.activity.result.ActivityResultLauncher
import com.example.location_source.view.MapsActivity
import com.example.location_source.view.MapsRoutingActivity

class ToMapsViewModel(
    private val context: Context,
    private val addActivityResultLauncher: ActivityResultLauncher<Intent>
) : BaseObservable() {

    fun toMapActivity() {
        val intent = Intent(context, MapsActivity::class.java)
        addActivityResultLauncher.launch(intent)
    }
    fun toMapRoutActivity() {
        val intent = Intent(context, MapsRoutingActivity::class.java)
        addActivityResultLauncher.launch(intent)
    }
}

