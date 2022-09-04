package com.scrip0.umassmaps.api.entities

data class Segment(
    val distance: Double,
    val duration: Double,
    val steps: List<Step>
)