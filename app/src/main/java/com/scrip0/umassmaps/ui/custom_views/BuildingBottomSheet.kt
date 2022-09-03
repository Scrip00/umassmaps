package com.scrip0.umassmaps.ui.custom_views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.scrip0.umassmaps.R

class BuildingBottomSheet : BottomSheetDialogFragment() {
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? = inflater.inflate(R.layout.building_bottom_sheet, container, false)
}