package com.scrip0.umassmaps.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.scrip0.umassmaps.R
import com.scrip0.umassmaps.db.entities.Building
import com.scrip0.umassmaps.utils.GeoUtils
import kotlinx.android.synthetic.main.item_search_result.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchResultsAdapter : RecyclerView.Adapter<SearchResultsAdapter.SearchResultsViewHolder>() {

	private var onItemClickListener: ((Building) -> Unit)? = null

	inner class SearchResultsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

	private val diffCallback = object : DiffUtil.ItemCallback<Building>() {
		override fun areItemsTheSame(oldItem: Building, newItem: Building): Boolean {
			return oldItem.id == newItem.id
		}

		override fun areContentsTheSame(oldItem: Building, newItem: Building): Boolean {
			return oldItem.hashCode() == newItem.hashCode()
		}
	}

	private val differ = AsyncListDiffer(this, diffCallback)

	fun submitList(list: List<Building>) = differ.submitList(list)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultsViewHolder {
		return SearchResultsViewHolder(
			LayoutInflater.from(parent.context).inflate(
				R.layout.item_search_result,
				parent,
				false
			)
		)
	}

	fun setOnItemClickListener(onItemClickListener: (Building) -> (Unit)) {
		this.onItemClickListener = onItemClickListener
	}

	override fun onBindViewHolder(holder: SearchResultsViewHolder, position: Int) {
		val building = differ.currentList[position]
		holder.itemView.apply {
			tvBuildingName.text = building.name
			val resId = Building.getBuildingIcon(building.type)
			if (resId != -1) ivIcon.setImageResource(Building.getBuildingIcon(building.type))
			CoroutineScope(Dispatchers.Default).launch {
				val locationAddress =
					GeoUtils.getAddress(context, building.latitude, building.longitude)
				withContext(Dispatchers.Main) {
					if (locationAddress.isEmpty()) {
						tvAddress.visibility = View.GONE
					} else {
						tvAddress.text = locationAddress
					}
				}
			}

			setOnClickListener {
				onItemClickListener?.invoke(building)
			}
		}

	}

	override fun getItemCount(): Int {
		return differ.currentList.size
	}
}