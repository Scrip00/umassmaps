package com.scrip0.umassmaps.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scrip0.umassmaps.data.entities.Building
import com.scrip0.umassmaps.data.remote.BuildingDatabase
import com.scrip0.umassmaps.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
	private val buildingDatabase: BuildingDatabase
) : ViewModel() {

	private val _buildingsLiveData = MutableLiveData<Resource<List<Building>>>()
	val buildingsLiveData: LiveData<Resource<List<Building>>> = _buildingsLiveData

	init {
		loadBuildings()
	}

	private fun loadBuildings() {
		_buildingsLiveData.postValue(Resource.loading(null))
		viewModelScope.launch {
			_buildingsLiveData.postValue(buildingDatabase.getAllBuildings())
		}
	}
}