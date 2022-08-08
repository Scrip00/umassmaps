package com.scrip0.umassmaps.db.remote

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.scrip0.umassmaps.db.entities.Building
import com.scrip0.umassmaps.other.Constants.BUILDING_COLLECTION
import com.scrip0.umassmaps.other.Resource
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class RemoteBuildingDatabase @Inject constructor(
	firestore: FirebaseFirestore
) {
	private val buildingCollection = firestore.collection(BUILDING_COLLECTION)

	fun subscribeToReaTimeUpdates(
		dataUpdated: (List<Building>) -> Unit
	) {
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
				dataUpdated(list)
			}
		}
	}
}