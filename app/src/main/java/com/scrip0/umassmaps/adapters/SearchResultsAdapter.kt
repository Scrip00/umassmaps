package com.scrip0.umassmaps.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.scrip0.umassmaps.R
import com.scrip0.umassmaps.db.entities.Building
import kotlinx.android.synthetic.main.item_search_result.view.*

class SearchResultsAdapter : RecyclerView.Adapter<SearchResultsAdapter.SearchResultsViewHolder>() {

	inner class SearchResultsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

	val diffCallback = object : DiffUtil.ItemCallback<Building>() {
		override fun areItemsTheSame(oldItem: Building, newItem: Building): Boolean {
			return oldItem.id == newItem.id
		}

		override fun areContentsTheSame(oldItem: Building, newItem: Building): Boolean {
			return oldItem.hashCode() == newItem.hashCode()
		}
	}

	val differ = AsyncListDiffer(this, diffCallback)

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

	override fun onBindViewHolder(holder: SearchResultsViewHolder, position: Int) {
		val building = differ.currentList[position]
		holder.itemView.apply {
			textView.text = building.name
		}
	}

	override fun getItemCount(): Int {
		return differ.currentList.size
	}
}