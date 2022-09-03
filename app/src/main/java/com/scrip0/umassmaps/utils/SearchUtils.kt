package com.scrip0.umassmaps.utils

import com.scrip0.umassmaps.db.entities.Building
import com.scrip0.umassmaps.other.Constants.STRING_COMPARISON_PRECISION
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchUtils {

	companion object {
		fun searchForWordOccurrence(query: String?, list: List<Building>?): List<Building> {
			list ?: return emptyList()
			query ?: return list
			val results = mutableListOf<Pair>()
			val precision = STRING_COMPARISON_PRECISION

			for (item in list) {
				var maxAccuracy = 0.0
				for (word in item.name.split("\\s".toRegex())) {
					val accuracy =
						StringUtils.compareStrings(query.lowercase(), word.lowercase())
					if (accuracy > maxAccuracy) maxAccuracy = accuracy
				}
				if (maxAccuracy >= precision) results.add(Pair(maxAccuracy, item))
			}

			results.sortByDescending { it.accuracy }

			return results.map { it.building }
		}
	}

	data class Pair(
		val accuracy: Double,
		val building: Building
	)
}