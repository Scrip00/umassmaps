package com.scrip0.umassmaps.data.entities

import com.google.android.gms.maps.model.LatLng

data class Building(
	val id: String = "",
	val location: LatLng = LatLng(0.0, 0.0),
	val name: String = "",
	val imageUrl: String = "",
	val description: String = "",
	val type: Int = 0
)
