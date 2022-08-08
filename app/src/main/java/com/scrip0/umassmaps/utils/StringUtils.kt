package com.scrip0.umassmaps.utils

import kotlin.math.max
import kotlin.math.min

// Credit to this guy https://www.techiedelight.com/find-similarities-between-two-strings-in-kotlin/

class StringUtils {

	companion object {
		private fun getLevenshteinDistance(x: String, y: String): Double {
			val m = x.length
			val n = y.length
			val distance = Array(m + 1) { DoubleArray(n + 1) }
			for (i in 1..m) {
				distance[i][0] = i.toDouble()
			}
			for (j in 1..n) {
				distance[0][j] = j.toDouble()
			}
			var cost: Double
			for (i in 1..m) {
				for (j in 1..n) {
					cost = if (x[i - 1] == y[j - 1]) 0.0 else 1.0
					distance[i][j] = min(
						min(distance[i - 1][j] + 1, distance[i][j - 1] + 0.7),
						distance[i - 1][j - 1] + cost
					)
				}
			}
			return distance[m][n]
		}

		fun compareStrings(str1: String, str2: String): Double {
			val maxLen = max(str1.length, str2.length)

			return if (maxLen > 0) {
				(maxLen * 1.0 - getLevenshteinDistance(str1, str2)) / maxLen * 1.0
			} else 1.0
		}
	}
}