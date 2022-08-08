package com.scrip0.umassmaps.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrip0.umassmaps.db.entities.Building
import com.scrip0.umassmaps.other.Resource
import com.scrip0.umassmaps.repositories.BuildingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
	private val buildingRepository: BuildingRepository
) : ViewModel() {

	private val _buildingsLiveData = MutableLiveData<Resource<List<Building>>>()
	val buildingsLiveData: LiveData<Resource<List<Building>>> = _buildingsLiveData

	val currentBuilding = MutableLiveData<Building>()

	var sortType: Int? = null

	init {
		subscribeToReaTimeUpdates()
	}

	private fun subscribeToReaTimeUpdates() {
		_buildingsLiveData.postValue(Resource.loading(null))
		buildingRepository.subscribeToReaTimeUpdates()
		viewModelScope.launch {
			buildingRepository.getAllBuildings(_buildingsLiveData)
		}
	}

	fun sortBuildings(sortType: Int?) {
		_buildingsLiveData.postValue(Resource.loading(null))
		sortType?.let {
			viewModelScope.launch {
				_buildingsLiveData.postValue(buildingRepository.getBuildingsSortedByType(sortType!!))
			}
			return
		}
		viewModelScope.launch {
			buildingRepository.getAllBuildings(_buildingsLiveData)
		}
	}
}