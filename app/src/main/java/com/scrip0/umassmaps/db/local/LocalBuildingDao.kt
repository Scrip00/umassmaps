package com.scrip0.umassmaps.db.local

import androidx.room.*
import com.scrip0.umassmaps.db.entities.Building
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalBuildingDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun upsertBuilding(building: Building)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun upsertAllBuildings(buildings: List<Building>)

	@Delete
	suspend fun deleteBuilding(building: Building)

	@Query("DELETE FROM building_table WHERE id NOT IN (:list)")
	suspend fun deleteMissingBuildings(list: List<String>)

	@Query("SELECT * FROM building_table")
	fun getAllBuildings(): Flow<List<Building>>
}