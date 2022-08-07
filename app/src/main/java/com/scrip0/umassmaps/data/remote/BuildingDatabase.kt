package com.scrip0.umassmaps.data.remote

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.scrip0.umassmaps.data.entities.Building
import com.scrip0.umassmaps.other.Constants.BUILDING_COLLECTION
import com.scrip0.umassmaps.other.Resource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BuildingDatabase @Inject constructor(
	firestore: FirebaseFirestore
) {
	private val buildingCollection = firestore.collection(BUILDING_COLLECTION)

	suspend fun getAllBuildings(): Resource<List<Building>> {
		return try {
			Resource.success(buildingCollection.get().await().toObjects(Building::class.java))
		} catch (e: Exception) {
			Resource.error(e.message ?: "Failed to load data", null)
		}
	}

	fun subscribeToReaTimeUpdates(_buildingsLiveData: MutableLiveData<Resource<List<Building>>>) {
		buildingCollection.addSnapshotListener { value, error ->
			error?.let {
				Resource.error(it.message ?: "Failed to load data", null)
				return@addSnapshotListener
			}
			value?.let {
				val list = mutableListOf<Building>()
				for (doc in it) {
					val building = doc.toObject<Building>()
					list.add(building)
				}
				_buildingsLiveData.postValue(Resource.success(list))
			}
		}
	}
}