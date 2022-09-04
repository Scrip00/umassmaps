package com.scrip0.umassmaps.api.entities

data class Step(
    val distance: Double,
    val duration: Double,
    val instruction: String,
    val name: String,
    val type: Int,
    val way_points: List<Int>
)