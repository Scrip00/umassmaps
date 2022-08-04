package com.scrip0.umassmaps.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.scrip0.umassmaps.data.entities.Building
import com.scrip0.umassmaps.other.Constants.BUILDING_COLLECTION
import com.scrip0.umassmaps.other.Resource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BuildingDatabase @Inject constructor(
	firestore: FirebaseFirestore
) {
	val buildingCollection = firestore.collection(BUILDING_COLLECTION)

	suspend fun getAllBuildings(): Resource<List<Building>> {
		return try {
			Resource.success(buildingCollection.get().await().toObjects(Building::class.java))
		} catch (e: Exception) {
			Resource.error(e.message ?: "Failed to load data", null)
		}
	}
}