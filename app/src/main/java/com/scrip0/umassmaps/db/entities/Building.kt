package com.scrip0.umassmaps.db.entities

import android.view.View
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.scrip0.umassmaps.R

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
	companion object {
		fun getBuildingIcon(id: Int?): Int {
			id ?: return -1
			return when (id) {
				Type.DORM -> R.drawable.ic_dorm
				Type.STUDY -> R.drawable.ic_study
				Type.LIBRARY -> R.drawable.ic_library
				Type.SPORT -> R.drawable.ic_sport
				Type.PARKING -> R.drawable.ic_parking
				Type.FOOD -> R.drawable.ic_food
				else -> return -1
			}
		}
	}

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
