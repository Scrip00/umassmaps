package com.scrip0.umassmaps.api.entities

data class Metadata(
	val attribution: String,
	val engine: Engine,
	val query: Query,
	val service: String,
	val timestamp: Long
)