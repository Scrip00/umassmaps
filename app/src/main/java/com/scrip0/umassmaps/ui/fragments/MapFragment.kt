package com.scrip0.umassmaps.ui.fragments

import android.os.Bundle
import android.util.Log
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
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.scrip0.umassmaps.R
import com.scrip0.umassmaps.other.Constants.MAP_ZOOM
import com.scrip0.umassmaps.other.Status
import com.scrip0.umassmaps.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_map.*

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map) {

	private var map: GoogleMap? = null

	private val viewModel: MainViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		mapView.onCreate(savedInstanceState)

		mapView.getMapAsync {
			map = it
			addLocationBorders()
			moveCameraToLocation()
			subscribeToObservers()
		}
	}

	private fun subscribeToObservers() {
		viewModel.buildingsLiveData.observe(viewLifecycleOwner) { result ->
			when (result.status) {
				Status.SUCCESS -> {
					mapProgressBar.isVisible = false
					Log.d("LOLLMAO17", "HELPME")
				}
				Status.ERROR -> Toast.makeText(
					context,
					"Cannot load the data: ${result.message}",
					Toast.LENGTH_LONG
				).show()
				Status.LOADING -> {
					mapProgressBar.isVisible = true
					Log.d("LOLLMAO17", "HELPME")
				}
			}
		}
	}

	private fun addLocationBorders() {
		val layer = GeoJsonLayer(map, R.raw.umass_borders, context)
		layer.defaultPolygonStyle.apply {
			strokeColor = ContextCompat.getColor(requireContext(), R.color.vine)
			strokeWidth = 10f
		}
		layer.addLayerToMap()
		val sydney = LatLng(100.0, 0.0)
		map?.addMarker(
			MarkerOptions()
				.position(sydney)
				.title("Marker in Sydney")
		)
	}

	private fun moveCameraToLocation() {
		map?.animateCamera(
			CameraUpdateFactory.newLatLngZoom(
				LatLng(42.38695, -72.5231),
				MAP_ZOOM
			)
		)
	}

	override fun onResume() {
		super.onResume()
		mapView?.onResume()
		mapView.getMapAsync {
			map = it
//			map?.clear()
		}
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