package ru.netology.yandexmaps.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collectLatest
import ru.netology.yandexmaps.R
import ru.netology.yandexmaps.adapter.Listener
import ru.netology.yandexmaps.adapter.PointAdapter
import ru.netology.yandexmaps.databinding.PointFragmentBinding
import ru.netology.yandexmaps.dto.Point
import ru.netology.yandexmaps.viewmodel.YandexMapViewModel

class PointFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = PointFragmentBinding.inflate(inflater, container, false)
        val viewModel by viewModels<YandexMapViewModel>()

        val adapter = PointAdapter(object : Listener{
            override fun onClick(point: Point) {
                findNavController().navigate(
                    R.id.action_pointFragment_to_mapFragment,
                    bundleOf(
                        MapFragment.LAT_KEY to point.lat,
                        MapFragment.LONG_KEY to point.long
                    )
                )
            }

            override fun onDelete(point: Point) {
                viewModel.deleteById(point.id)

            }

            override fun onEdit(point: Point) {
                Dialog.newInstance(lat = point.lat, long = point.long, id = point.id)
                    .show(childFragmentManager, null)
            }

        })

        binding.list.adapter = adapter

        viewLifecycleOwner.lifecycle.coroutineScope.launchWhenCreated {
            viewModel.points.collectLatest { points->
                adapter.submitList(points)
                binding.empty.isVisible = points.isEmpty()
            }
        }
        return binding.root

    }
}