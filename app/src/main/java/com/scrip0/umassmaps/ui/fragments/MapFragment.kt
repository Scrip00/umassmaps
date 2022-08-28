package com.scrip0.umassmaps.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.SearchView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.maps.android.collections.GroundOverlayManager
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.collections.PolygonManager
import com.google.maps.android.collections.PolylineManager
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.scrip0.umassmaps.R
import com.scrip0.umassmaps.adapters.SearchResultsAdapter
import com.scrip0.umassmaps.db.entities.Building
import com.scrip0.umassmaps.db.entities.Type.DORM
import com.scrip0.umassmaps.db.entities.Type.FOOD
import com.scrip0.umassmaps.db.entities.Type.LIBRARY
import com.scrip0.umassmaps.db.entities.Type.PARKING
import com.scrip0.umassmaps.db.entities.Type.SPORT
import com.scrip0.umassmaps.db.entities.Type.STUDY
import com.scrip0.umassmaps.other.Constants.BUILDINGS_ICON_SIZE
import com.scrip0.umassmaps.other.Constants.MAP_ZOOM
import com.scrip0.umassmaps.other.Constants.SEARCH_DELAY
import com.scrip0.umassmaps.other.Status
import com.scrip0.umassmaps.ui.viewmodels.MainViewModel
import com.scrip0.umassmaps.utils.SearchUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.sort_option.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject


@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map) {

	private var map: GoogleMap? = null

	private val viewModel: MainViewModel by viewModels()

	private var clicked: GeoJsonLayer? = null

	private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		bottomSheetBehavior = BottomSheetBehavior.from(buildingInfoSheet)

		mapView.onCreate(savedInstanceState)

		setupBottomSheet()
		setupSortOptions()

		mapView.getMapAsync {
			map = it
			moveCameraToLocation()
			subscribeToObservers()
		}

		spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
				when (pos) {
					0 -> {
						viewModel.sortBuildings(DORM)
						viewModel.sortType = DORM
					}
					1 -> {
						viewModel.sortBuildings(STUDY)
						viewModel.sortType = STUDY
					}
					2 -> {
						viewModel.sortBuildings(LIBRARY)
						viewModel.sortType = LIBRARY
					}
					3 -> {
						viewModel.sortBuildings(FOOD)
						viewModel.sortType = FOOD
					}
					4 -> {
						viewModel.sortBuildings(SPORT)
						viewModel.sortType = SPORT
					}
					5 -> {
						viewModel.sortBuildings(PARKING)
						viewModel.sortType = PARKING
					}
				}
			}

			override fun onNothingSelected(p0: AdapterView<*>?) {
				viewModel.sortBuildings(null)
				viewModel.sortType = null
			}
		}
	}

	private fun setupSortOptions() {
		val sortTypes = requireContext().resources.getStringArray(R.array.filter_options)
		for (type in sortTypes) {
			val sortOption = View.inflate(requireContext(), R.layout.sort_option, null)
			sortOption.tvType.text = type
			sortOptions.addView(sortOption)
		}
	}

	private fun setupSearch() {
		val searchResultsAdapter = SearchResultsAdapter()
		rvSearchResults.apply {
			adapter = searchResultsAdapter
			layoutManager = LinearLayoutManager(requireContext())
		}

		val list = viewModel.buildingsLiveData.value?.data
		var job: Job? = null

		searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
			override fun onQueryTextSubmit(query: String?): Boolean {
				search(query)
				return false
			}

			override fun onQueryTextChange(newText: String?): Boolean {
				job?.cancel()
				job = MainScope().launch {
					delay(SEARCH_DELAY)
					search(newText)
				}
				return false
			}

			fun search(query: String?) {
				val searchResults = SearchUtils.searchForWordOccurrence(query, list)
				if (searchResults != null) searchResultsAdapter.submitList(searchResults)
			}
		})
	}

	private fun setupBottomSheet() {
		appBar.alpha = 0f
		appBarLayout.visibility = View.GONE
		bottomSheetBehavior.addBottomSheetCallback(object :
			BottomSheetBehavior.BottomSheetCallback() {
			override fun onStateChanged(bottomSheet: View, newState: Int) {
				when (newState) {
					STATE_HIDDEN -> {
						appBarLayout.visibility = View.GONE
						onBuildingClicked()
					}
					STATE_COLLAPSED -> appBarLayout.visibility = View.VISIBLE
				}
			}

			override fun onSlide(bottomSheet: View, slideOffset: Float) {
				appBar.alpha = slideOffset
				ivBuilding.y = appBar.height * slideOffset
			}
		})

		bottomSheetBehavior.state = STATE_HIDDEN
	}

	private fun subscribeToObservers() {
		viewModel.buildingsLiveData.observe(viewLifecycleOwner) { result ->
			when (result.status) {
				Status.SUCCESS -> {
					mapProgressBar.isVisible = false
					addAllBuildings(result?.data)
					setupSearch()
				}
				Status.ERROR -> {
					mapProgressBar.isVisible = false
					Toast.makeText(
						context,
						"Cannot load the data: ${result.message}",
						Toast.LENGTH_LONG
					).show()
				}
				Status.LOADING -> {
					mapProgressBar.isVisible = true
				}
			}
		}

		viewModel.currentBuilding.observe(viewLifecycleOwner) { building ->
			moveCameraToLocation(LatLng(building.latitude, building.longitude))
		}
	}

	@SuppressLint("PotentialBehaviorOverride")
	private fun addAllBuildings(list: List<Building>?) {
		map?.clear()

		val markerManager = MarkerManager(map)
		val groundOverlayManager = GroundOverlayManager(map!!)
		val polygonManager = PolygonManager(map)
		val polylineManager = PolylineManager(map!!)

		val markerCollection = markerManager.newCollection()

		val border = GeoJsonLayer(
			map, R.raw.umass_borders, context, markerManager,
			polygonManager,
			polylineManager,
			groundOverlayManager
		)
		border.defaultPolygonStyle.apply {
			strokeColor = ContextCompat.getColor(requireContext(), R.color.vine)
			strokeWidth = 10f
			zIndex = 1F
		}
		border.addLayerToMap()
		border.setOnFeatureClickListener {
			onBuildingClicked()
		}

		val hashMap = HashMap<Marker, GeoJsonLayer>()
		val markerIdMap = HashMap<String, String>()
		list?.forEach { building ->
			val marker = markerCollection.addMarker(
				MarkerOptions()
					.position(LatLng(building.latitude - 0.00003, building.longitude))
					.title(building.name)
					.zIndex(2F)
					.icon(getBuildingIcon(building.type))
			)
			markerIdMap[marker.id] = building.id
			val layer = GeoJsonLayer(
				map,
				JSONObject(building.shape),
				markerManager,
				polygonManager,
				polylineManager,
				groundOverlayManager
			)
			hashMap[marker] = layer
			layer.defaultPolygonStyle.apply {
				strokeColor = Color.BLACK
				strokeWidth = 4f
				fillColor = Color.GRAY
				zIndex = 3F
			}
			layer.addLayerToMap()
			layer.setOnFeatureClickListener {
				marker.showInfoWindow()
				loadBottomSheet(building)
				onBuildingClicked(layer)
			}
		}

		markerCollection.setOnMarkerClickListener {
			val layer = hashMap[it]
			layer?.apply {
				onBuildingClicked(layer)
				loadBottomSheet(list?.find { building ->
					building.id == markerIdMap[it.id]
				})
			}
			false
		}

		map?.setOnMapClickListener {
			onBuildingClicked()
		}
	}

	private fun getBuildingIcon(type: Int): BitmapDescriptor? {
		val vectorResId = when (type) {
			DORM -> R.drawable.ic_dorm
			STUDY -> R.drawable.ic_study
			LIBRARY -> R.drawable.ic_library
			SPORT -> R.drawable.ic_sport
			PARKING -> R.drawable.ic_parking
			FOOD -> R.drawable.ic_food
			else -> return null
		}
		val vectorDrawable = ContextCompat.getDrawable(requireContext(), vectorResId)

		vectorDrawable?.setBounds(
			0,
			0,
			vectorDrawable.intrinsicWidth,
			vectorDrawable.intrinsicHeight
		)
		val bitmap = Bitmap.createBitmap(
			vectorDrawable?.intrinsicWidth ?: 0,
			vectorDrawable?.intrinsicHeight ?: 0,
			Bitmap.Config.ARGB_8888
		)
		val canvas = Canvas(bitmap)
		vectorDrawable?.draw(canvas)

		val resizedBitmap = Bitmap.createScaledBitmap(
			bitmap, BUILDINGS_ICON_SIZE, BUILDINGS_ICON_SIZE, false
		)

		return BitmapDescriptorFactory.fromBitmap(resizedBitmap)
	}

	private fun onBuildingClicked(layer: GeoJsonLayer? = null) {
		if (clicked?.equals(layer) == true) return

		clicked?.apply {
			defaultPolygonStyle.apply {
				fillColor = Color.GRAY
			}
		}
		clicked = layer
		clicked?.apply {
			defaultPolygonStyle.apply {
				fillColor = Color.RED
			}
		}
	}

	private fun loadBottomSheet(building: Building?) {
		if (bottomSheetBehavior.state == STATE_HIDDEN)
			bottomSheetBehavior.state = STATE_COLLAPSED

		Glide.with(requireContext()).load(building?.imageUrl).into(ivBuilding)
		tvAppBar.text = building?.name
		tvSheet.text = building?.name
	}

	private fun moveCameraToLocation(pos: LatLng = LatLng(42.38695, -72.5231)) {
		map?.animateCamera(
			CameraUpdateFactory.newLatLngZoom(
				pos,
				MAP_ZOOM
			)
		)
	}

	override fun onResume() {
		super.onResume()
		mapView?.onResume()
	}

	override fun onStart() {
		super.onStart()
		mapView?.onStart()
	}

	override fun onStop() {
		super.onStop()
		mapView?.onStop()
	}

	override fun onPause() {
		super.onPause()
		mapView?.onPause()
	}

	override fun onLowMemory() {
		super.onLowMemory()
		mapView?.onLowMemory()
	}

	override fun onDestroy() {
		super.onDestroy()
		mapView?.onDestroy()
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		mapView?.onSaveInstanceState(outState)
	}
}