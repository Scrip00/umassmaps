package com.scrip0.umassmaps.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "building_table")
data class Building(
	@PrimaryKey
	val id: String = "",
	val latitude: Double = 0.0,
	val longitude: Double = 0.0,
	val name: String = "",
	val imageUrl: String = "",
	val description: String = "",
	val type: Int = 0,
	val shape: String = ""
) {
	override fun equals(other: Any?): Boolean {
		return super.hashCode() == other.hashCode()
	}

	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + latitude.hashCode()
		result = 31 * result + longitude.hashCode()
		result = 31 * result + name.hashCode()
		result = 31 * result + imageUrl.hashCode()
		result = 31 * result + description.hashCode()
		result = 31 * result + type
		result = 31 * result + shape.hashCode()
		return result
	}
}

object Type {
	const val DORM = 0
	const val STUDY = 1
	const val LIBRARY = 2
	const val SPORT = 3
	const val PARKING = 4
	const val FOOD = 5
}
