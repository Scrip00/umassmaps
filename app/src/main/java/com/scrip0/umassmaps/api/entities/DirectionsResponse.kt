package com.scrip0.umassmaps.api.entities

data class DirectionsResponse(
    val bbox: List<Double>,
    val features: List<Feature>,
    val metadata: Metadata,
    val type: String
)