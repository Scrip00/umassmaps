package com.scrip0.umassmaps.ui.viewmodels

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrip0.umassmaps.db.entities.Building
import com.scrip0.umassmaps.other.Resource
import com.scrip0.umassmaps.other.Status
import com.scrip0.umassmaps.repositories.BuildingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
	private val buildingRepository: BuildingRepository
) : ViewModel() {

	private val _buildingsLiveData = MutableLiveData<Resource<List<Building>>>()
	val buildingsLiveData: LiveData<Resource<List<Building>>> = _buildingsLiveData

	val currentBuilding = MutableLiveData<Building>()

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
}