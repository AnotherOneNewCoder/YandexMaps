package ru.netology.yandexmaps.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.ui_view.ViewProvider
import kotlinx.coroutines.flow.collectLatest
import ru.netology.yandexmaps.R
import ru.netology.yandexmaps.databinding.MapFragmentBinding
import ru.netology.yandexmaps.databinding.PointBinding
import ru.netology.yandexmaps.utils.CommonUtils.showToast
import ru.netology.yandexmaps.viewmodel.YandexMapViewModel


class MapFragment : Fragment(), UserLocationObjectListener, InputListener {

    private lateinit var binding: MapFragmentBinding
    private lateinit var map: Map
    private lateinit var userLocation: UserLocationLayer
    private lateinit var mapView: MapView


    companion object {
        private const val ZOOM_STEP = 1f
        private val SMOOTH_ANIMATION = Animation(Animation.Type.SMOOTH, 0.4f)
        const val LAT_KEY = "LAT_KEY"
        const val LONG_KEY = "LONG_KEY"

        // временные точки для теста
        private val START_ANIMATION = Animation(Animation.Type.LINEAR, 1f)
        private val START_POSITION = CameraPosition(Point(54.707590, 20.508898), 15f, 0f, 0f)
    }

    private val viewModel by viewModels<YandexMapViewModel>()

    // удаление метки на карте
    private val pointTapListener = MapObjectTapListener { mapObject, _ ->
        viewModel.deleteById(mapObject.userData as Long)
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MapFragmentBinding.inflate(inflater, container, false)
        val mapWindow = binding.mapView.mapWindow
        map = mapWindow.map
        mapView = binding.mapView
        val mapKit: MapKit = MapKitFactory.getInstance()
        requestLocationPermission()
        userLocation = mapKit.createUserLocationLayer(mapWindow)
        val probki = mapKit.createTrafficLayer(mapWindow)
        userLocation.setObjectListener(this)
        map.addInputListener(this)

        var onOffTraffic = false
        var onOffUserLocation = false

        val pointCollection = map.mapObjects.addCollection()
        viewLifecycleOwner.lifecycle.coroutineScope.launchWhenCreated {
            viewModel.points.collectLatest { points ->
                pointCollection.clear()
                points.forEach {
                    val pointBinding = PointBinding.inflate(layoutInflater)
                    pointBinding.title.text = it.title
                    pointCollection.addPlacemark(
                        Point(it.lat, it.long),
                        ViewProvider(pointBinding.root)
                    ).apply {
                        userData = it.id
                    }
                }
            }
        }
        pointCollection.addTapListener(pointTapListener)
        // переход камеры к точке
        val arguments = arguments
        if (arguments != null &&
            arguments.containsKey(LAT_KEY) &&
            arguments.containsKey(LONG_KEY)
        ) {
            val cameraPosition = map.cameraPosition
            map.move(
                CameraPosition(
                    Point(arguments.getDouble(LAT_KEY), arguments.getDouble(LONG_KEY)),
                    10F,
                    cameraPosition.azimuth,
                    cameraPosition.tilt,
                )
            )
            arguments.remove(LAT_KEY)
            arguments.remove(LONG_KEY)
        }





        binding.apply {
            minus.setOnClickListener { changeZoomByStep(-ZOOM_STEP) }
            plus.setOnClickListener { changeZoomByStep(ZOOM_STEP) }
            dataBase.setOnClickListener {
                findNavController().navigate(R.id.action_mapFragment_to_pointFragment)
            }
            probkibtn.setOnClickListener {
                when (onOffTraffic) {
                    true -> {
                        probki.isTrafficVisible = false
                        onOffTraffic = false
                        val drawable = ContextCompat.getDrawable(requireContext(), R.color.grey)
                        cardTraffic.foreground = drawable

                    }

                    false -> {
                        probki.isTrafficVisible = true
                        onOffTraffic = true
                        val drawable = ContextCompat.getDrawable(requireContext(), R.color.yellow)
                        cardTraffic.foreground = drawable

                    }
                }
            }
            location.setOnClickListener {
                when (onOffUserLocation) {
                    true -> {

                        userLocation.isVisible = false
                        userLocation.isHeadingEnabled = false
                        onOffUserLocation = false
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

                        onOffUserLocation = true
                    }

                }
            }
        }


        return binding.root
    }
    // запрос разрешений
    private fun requestLocationPermission() {
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
    //приближение/отдаление камеры
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
    // отображение местоположения
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

    }

    override fun onMapTap(map: Map, point: Point) = Unit
    // добавление точки при длительном удержании на карте
    override fun onMapLongTap(map: Map, point: Point) {
        Dialog.newInstance(lat = point.latitude, long = point.longitude)
            .show(childFragmentManager, null)
    }
}