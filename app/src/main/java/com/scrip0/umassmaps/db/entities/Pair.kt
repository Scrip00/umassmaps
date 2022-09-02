package com.scrip0.umassmaps.db.entities

import java.io.Serializable

data class Pair<A, B>(
	var first: A,
	var second: B
) : Serializable