package com.scrip0.umassmaps.data.entities

data class Building(
	val id: String = "",
	val latitude: Double = 0.0,
	val longitude: Double = 0.0,
	val name: String = "",
	val imageUrl: String = "",
	val description: String = "",
	val type: Int = 0,
	val shape: String = ""
)
