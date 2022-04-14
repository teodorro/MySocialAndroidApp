package com.example.mysocialandroidapp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.example.mysocialandroidapp.R
import com.example.mysocialandroidapp.databinding.FragmentMapEventBinding
import com.example.mysocialandroidapp.databinding.FragmentMapPostBinding
import com.example.mysocialandroidapp.util.icon
import com.example.mysocialandroidapp.viewmodel.EventsViewModel
import com.example.mysocialandroidapp.viewmodel.PostsViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.collections.MarkerManager
import com.google.maps.android.ktx.awaitAnimateCamera
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.model.cameraPosition
import com.google.maps.android.ktx.utils.collection.addMarker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MapPostFragment : Fragment() {
    private lateinit var googleMap: GoogleMap

    private var _binding: FragmentMapPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PostsViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    private lateinit var markerManager: MarkerManager
    private lateinit var markerCollection: MarkerManager.Collection
    private var selectedMarker: Marker? = null

    private lateinit var appBarMenu: Menu

    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                googleMap.apply {
                    isMyLocationEnabled = true
                    uiSettings.isMyLocationButtonEnabled = true
                }
            } else {
                // TODO: show sorry dialog
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_new_delete, menu)
        appBarMenu = menu
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapPostBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_location)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        val markersInitialized = this::markerManager.isInitialized

        lifecycle.coroutineScope.launchWhenCreated {
            if (!markersInitialized) {
                setMap(mapFragment)
                setupGeoPosition()
                markerManager = MarkerManager(googleMap)
                markerCollection = markerManager.newCollection("markCollection")
                markerCollection.setOnMarkerClickListener {
                    lifecycle.coroutineScope.launch {
                        moveCamera(it.position.latitude, it.position.longitude)
                    }
                    return@setOnMarkerClickListener true
                }
            }

            viewModel.edited.value?.coords?.let {
                moveCamera(it.lat, it.long)
                markerCollection.addMarker {
                    this.position(LatLng(it.lat, it.long))
                    this.icon(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_red_location_on_32
                        )!!
                    )
                }
                selectedMarker = markerCollection.markers.first()
                appBarMenu.setGroupVisible(R.id.point_exists, true)
            }
            if (viewModel.edited.value?.coords == null) {
                moveCamera(55.751999, 37.617734)
                appBarMenu.setGroupVisible(R.id.point_exists, false)
            }
        }
    }

    private suspend fun setMap(mapFragment: SupportMapFragment) {
        googleMap = mapFragment.awaitMap().apply {
            isTrafficEnabled = true
            isBuildingsEnabled = true

            uiSettings.apply {
                isZoomControlsEnabled = true
                setAllGesturesEnabled(true)
            }
        }
    }

    private fun setupGeoPosition() {
        when {
            // 1. Проверяем есть ли уже права
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                googleMap.apply {
                    isMyLocationEnabled = true
                    uiSettings.isMyLocationButtonEnabled = true
                }

                val fusedLocationProviderClient = LocationServices
                    .getFusedLocationProviderClient(requireActivity())

                fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                    println(it)
                }

                // map Click
                googleMap.setOnMapClickListener {
                    markerCollection.clear()
                    markerCollection.addMarker {
                        this.position(LatLng(it.latitude, it.longitude))
                        this.icon(
                            AppCompatResources.getDrawable(
                                requireContext(),
                                R.drawable.ic_red_location_on_32
                            )!!
                        )
                    }
                    selectedMarker = markerCollection.markers.first()
                    appBarMenu.setGroupVisible(R.id.point_exists, true)
                }
            }
            // 2. Должны показать обоснование необходимости прав
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // TODO: show rationale dialog
            }
            // 3. Запрашиваем права
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private suspend fun moveCamera(latitude: Double, longitude: Double) {
        val target = LatLng(latitude, longitude)

        googleMap.awaitAnimateCamera(
            CameraUpdateFactory.newCameraPosition(
                cameraPosition {
                    target(target)
                    zoom(15F)
                }
            ),
            500
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.save -> {
                binding.let{
                    viewModel.changeLocation(
                        selectedMarker?.position?.latitude,
                        selectedMarker?.position?.longitude
                    )
                    findNavController().navigateUp()
                }
                true
            }
            R.id.remove -> {
                markerCollection.clear()
                selectedMarker = null
                appBarMenu.setGroupVisible(R.id.point_exists, false)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }
}