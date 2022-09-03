package com.scrip0.umassmaps.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import java.util.*

class GeoUtils {
	companion object {

		fun getAddress(context: Context, lat: Double, lng: Double): String {
			var strAdd = ""
			val geocoder = Geocoder(context, Locale.getDefault())
			try {
				val addresses = geocoder.getFromLocation(lat, lng, 1)

				if (addresses != null) {
					val returnedAddress: Address = addresses[0]
					val strReturnedAddress = StringBuilder("")
					for (i in 0..returnedAddress.maxAddressLineIndex) {
						strReturnedAddress.append(returnedAddress.getAddressLine(i))
							.append("\n")
					}
					strAdd = strReturnedAddress.toString()
					Log.d("My Current loction address", strReturnedAddress.toString())
				} else {
					Log.d("my current loction address", "no address returned!")
				}
			} catch (e: Exception) {
				e.printStackTrace()
				Log.d("My Current loction address", "Canont get Address!")
			}
			return strAdd
		}
	}
}