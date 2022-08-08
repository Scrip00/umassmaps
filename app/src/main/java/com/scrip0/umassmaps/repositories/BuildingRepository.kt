package com.scrip0.umassmaps.repositories

import androidx.lifecycle.MutableLiveData
import com.scrip0.umassmaps.db.entities.Building
import com.scrip0.umassmaps.db.local.LocalBuildingDao
import com.scrip0.umassmaps.db.remote.RemoteBuildingDatabase
import com.scrip0.umassmaps.other.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class BuildingRepository @Inject constructor(
	private val remoteBuildingDatabase: RemoteBuildingDatabase,
	private val localBuildingDao: LocalBuildingDao
) {
	private var dataUpdated: ((List<Building>) -> Unit) = { list ->
		updateData(list)
	}

	fun subscribeToReaTimeUpdates() =
		remoteBuildingDatabase.subscribeToReaTimeUpdates(dataUpdated)

	suspend fun upsertBuildingLocal(building: Building) = localBuildingDao.upsertBuilding(building)

	suspend fun deleteBuildingLocal(building: Building) = localBuildingDao.deleteBuilding(building)

	suspend fun getAllBuildings(buildingsLiveData: MutableLiveData<Resource<List<Building>>>) {
		CoroutineScope(Dispatchers.IO).launch {
			localBuildingDao.getAllBuildings().collect { data ->
				buildingsLiveData.postValue(Resource.success(data))
			}
		}
	}

	private fun updateData(list: List<Building>) {
		CoroutineScope(Dispatchers.IO).launch {
			localBuildingDao.deleteMissingBuildings(list.map { it.id })
			localBuildingDao.upsertAllBuildings(list)
		}
	}
}