package com.example.location_source.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import com.example.location_source.R
import com.example.location_source.databinding.ActivityBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: ActivityBottomSheetBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sortBy: () -> Unit
    private lateinit var isSelected: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_bottom_sheet, container, false)
        binding.viewModel = BottomSheetViewModel()

        sharedPreferences = requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val orderOfList = sharedPreferences.getString("OrderOfList", null)
        if (orderOfList == "Ascending") {
            binding.viewModel?.ascendingOrderChecked?.set(true)
        } else if (orderOfList == "Descending") {
            binding.viewModel?.descendingOrderChecked?.set(true)
        }

        binding.btnApply.setOnClickListener {
            val selectedOrder = if (binding.viewModel?.ascendingOrderChecked?.get() == true) {
                "Ascending"
            } else {
                "Descending"
            }
            editor.putString("OrderOfList", selectedOrder)
            editor.apply()
            sortBy.invoke()
            dismiss()
        }
        binding.ivBack.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    companion object {
        fun newInstance(isSelected: String, sortBy: () -> Unit): BottomSheet {
            val fragment = BottomSheet()
            fragment.sortBy = sortBy
            fragment.isSelected = isSelected
            return fragment
        }
    }
}

class BottomSheetViewModel : ViewModel() {
    val ascendingOrderChecked = ObservableBoolean(false)
    val descendingOrderChecked = ObservableBoolean(false)
}
