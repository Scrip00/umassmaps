package com.scrip0.umassmaps.repositories

import com.google.android.gms.maps.model.LatLng
import com.scrip0.umassmaps.api.entities.RetrofitInstance

class DirectionsRepository {
	suspend fun getDirections(startLocation: LatLng, endLocation: LatLng) =
		RetrofitInstance.api.getDirections("${startLocation.longitude},${startLocation.latitude}", "${endLocation.longitude},${endLocation.latitude}")
}