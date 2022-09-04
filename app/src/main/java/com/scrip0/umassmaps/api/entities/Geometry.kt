package com.scrip0.umassmaps.api.entities

data class Geometry(
    val coordinates: List<List<Double>>,
    val type: String
)