package ru.netology.yandexmaps.adapter

import androidx.recyclerview.widget.RecyclerView
import ru.netology.yandexmaps.databinding.PointItemBinding
import ru.netology.yandexmaps.dto.Point

class PointViewHolder(
    private val binding: PointItemBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(point: Point) {
        binding.title.text = point.title
    }
}