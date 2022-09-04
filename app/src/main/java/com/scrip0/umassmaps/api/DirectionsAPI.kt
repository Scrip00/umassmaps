package com.scrip0.umassmaps.api

import  com.google.android.gms.maps.model.LatLng
import com.scrip0.umassmaps.api.entities.DirectionsResponse
import com.scrip0.umassmaps.other.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsAPI {

    @GET("v2/directions/foot-walking")
    suspend fun getDirections(
        @Query("start")
        startLocation: String,
        @Query("end")
        endLocation: String,
        @Query("api_key")
        apiKey: String = API_KEY
    ): Response<DirectionsResponse>

}