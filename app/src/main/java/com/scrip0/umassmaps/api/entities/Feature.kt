package com.scrip0.umassmaps.api.entities

data class Feature(
	val bbox: List<Double>,
	val geometry: Geometry,
	val properties: Properties,
	val type: String
)