package com.scrip0.umassmaps.api.entities

data class Query(
    val coordinates: List<List<Double>>,
    val format: String,
    val profile: String
)