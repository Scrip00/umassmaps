package com.scrip0.umassmaps.api.entities

data class Properties(
	val segments: List<Segment>,
	val summary: Summary,
	val way_points: List<Int>
)