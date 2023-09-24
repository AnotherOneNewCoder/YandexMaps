package ru.netology.yandexmaps.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.yandex.mapkit.Animation

import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import ru.netology.yandexmaps.R
import ru.netology.yandexmaps.databinding.MapFragmentBinding
import ru.netology.yandexmaps.utils.CommonUtils.showToast
import ru.netology.yandexmaps.utils.CommonUtils.createBitmapFromVector


class MapFragment : Fragment(), UserLocationObjectListener, InputListener {

    lateinit var binding: MapFragmentBinding
    lateinit var map: Map
    lateinit var userLocation: UserLocationLayer
    lateinit var mapView: MapView


    companion object {
        private const val ZOOM_STEP = 1f
        private val SMOOTH_ANIMATION = Animation(Animation.Type.SMOOTH, 0.4f)
        const val LAT_KEY = "LAT_KEY"
        const val LONG_KEY = "LONG_KEY"

        // временные точки для теста
        private val START_ANIMATION = Animation(Animation.Type.LINEAR, 1f)
        private val START_POSITION = CameraPosition(Point(54.707590, 20.508898), 15f, 0f, 0f)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MapFragmentBinding.inflate(inflater, container, false)
        val mapWindow = binding.mapView.mapWindow
        map = mapWindow.map
        mapView = binding.mapView
        val mapKit: MapKit = MapKitFactory.getInstance()
        requstLocationPermission()
        userLocation = mapKit.createUserLocationLayer(mapWindow)
        val probki = mapKit.createTrafficLayer(mapWindow)
        userLocation.setObjectListener(this)

        var OnOffTraffic = false
        var OnOffUserLocation = false



        binding.apply {
            minus.setOnClickListener { changeZoomByStep(-ZOOM_STEP) }
            plus.setOnClickListener { changeZoomByStep(ZOOM_STEP) }
            probkibtn.setOnClickListener {
                when (OnOffTraffic) {
                    true -> {
                        probki.isTrafficVisible = false
                        OnOffTraffic = false
                        val drawable = ContextCompat.getDrawable(requireContext(), R.color.grey)
                        cardTraffic.foreground = drawable

                    }

                    false -> {
                        probki.isTrafficVisible = true
                        OnOffTraffic = true
                        val drawable = ContextCompat.getDrawable(requireContext(), R.color.yellow)
                        cardTraffic.foreground = drawable

                    }
                }
            }
            location.setOnClickListener {
                when (OnOffUserLocation) {
                    true -> {

                        userLocation.isVisible = false
                        userLocation.isHeadingEnabled = false
                        OnOffUserLocation = false
                    }

                    false -> {

                        userLocation.isVisible = true
                        userLocation.isHeadingEnabled = true
                        val target = userLocation.cameraPosition()

                        if (target != null) {
                            map.move(
                                CameraPosition(
                                    target.target,
                                    target.zoom,
                                    target.azimuth,
                                    target.tilt
                                )
                            )
                        } else {
                            map.move(
                                START_POSITION,
                                START_ANIMATION
                            ) { requireContext().showToast("Initial camera move") }
                        }

                        OnOffUserLocation = true
                    }

                }
            }
        }

        return binding.root
    }

    private fun requstLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                0
            )
            return
        }
    }

    private fun changeZoomByStep(value: Float) {
        with(map.cameraPosition) {
            map.move(
                CameraPosition(target, zoom + value, azimuth, tilt),
                SMOOTH_ANIMATION,
                null,
            )
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
        MapKitFactory.getInstance().onStart()

    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()

    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocation.setAnchor(
            PointF(mapView.width * 0.5F, mapView.height * 0.5F),
            PointF(mapView.width * 0.5F, mapView.height * 0.5F),
        )
    }

    override fun onObjectRemoved(userLocationView: UserLocationView) {
        userLocationView.isValid
        with(map.cameraPosition) {
            map.move(
                START_POSITION,
                START_ANIMATION
            ) { requireContext().showToast("Initial camera move") }
        }
    }

    override fun onObjectUpdated(userLocationView: UserLocationView, objectEvent: ObjectEvent) {
        Unit
    }

    override fun onMapTap(map: Map, point: Point) {
        TODO("Not yet implemented")
    }

    override fun onMapLongTap(map: Map, point: Point) {
        TODO("Not yet implemented")
    }
}