package com.scrip0.umassmaps.db.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.scrip0.umassmaps.db.entities.Building

@Database(
	entities = [Building::class],
	version = 1
)
abstract class LocalBuildingDatabase : RoomDatabase() {

	abstract fun getBuildingDao(): LocalBuildingDao
}