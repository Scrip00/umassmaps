package com.scrip0.umassmaps.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.collections.GroundOverlayManager
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.collections.PolygonManager
import com.google.maps.android.collections.PolylineManager
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.scrip0.umassmaps.R
import com.scrip0.umassmaps.data.entities.Building
import com.scrip0.umassmaps.other.Constants.MAP_ZOOM
import com.scrip0.umassmaps.other.Status
import com.scrip0.umassmaps.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_map.*
import org.json.JSONObject

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map) {

	private var map: GoogleMap? = null

	private val viewModel: MainViewModel by viewModels()

	private var clicked = mutableListOf<GeoJsonLayer>()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		mapView.onCreate(savedInstanceState)

		mapView.getMapAsync {
			map = it
			moveCameraToLocation()
			subscribeToObservers()
		}
	}

	private fun subscribeToObservers() {
		viewModel.buildingsLiveData.observe(viewLifecycleOwner) { result ->
			when (result.status) {
				Status.SUCCESS -> {
					mapProgressBar.isVisible = false
					addAllMarkers(result?.data)
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
	private fun addAllMarkers(list: List<Building>?) {
		map?.clear()

		val markerManager = MarkerManager(map)
		val groundOverlayManager = GroundOverlayManager(map!!)
		val polygonManager = PolygonManager(map)
		val polylineManager = PolylineManager(map!!)

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
			clearClickedFeatures()
		}

		list?.forEach { building ->
			val marker = map?.addMarker(
				MarkerOptions()
					.position(LatLng(building.latitude, building.longitude))
					.title(building.name)
					.zIndex(2F)
			)
			val layer = GeoJsonLayer(
				map,
				JSONObject(building.shape),
				markerManager,
				polygonManager,
				polylineManager,
				groundOverlayManager
			)
			layer.defaultPolygonStyle.apply {
				strokeColor = Color.BLACK
				strokeWidth = 4f
				fillColor = Color.GRAY
				zIndex = 3F
			}
			layer.addLayerToMap()
			layer.setOnFeatureClickListener {
				layer.defaultPolygonStyle.apply {
					fillColor = Color.RED
				}
				clearClickedFeatures()
				clicked.add(layer)
			}
		}

		map?.setOnMapClickListener {
			clearClickedFeatures()
		}
	}

	private fun clearClickedFeatures() {
		while (clicked.isNotEmpty()) {
			val layer = clicked[0]
			clicked.removeAt(0)
			layer.defaultPolygonStyle.apply {
				fillColor = Color.GRAY
			}
		}
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