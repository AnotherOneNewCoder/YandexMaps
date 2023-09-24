package ru.netology.yandexmaps.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import ru.netology.yandexmaps.R
import ru.netology.yandexmaps.databinding.PointItemBinding
import ru.netology.yandexmaps.dto.Point
interface Listener {
    fun onClick(point: Point)
    fun onDelete(point: Point)
    fun onEdit(point: Point)
}
class PointAdapter(
    private val listener: Listener
): ListAdapter<Point, PointViewHolder>(PointDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {
        val binding = PointItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        val holder = PointViewHolder(binding)
        binding.apply {
            root.setOnClickListener {
                val point = getItem(holder.adapterPosition)
                listener.onClick(point)
            }
            menu.setOnClickListener {
                PopupMenu(root.context, it).apply {
                    inflate(R.menu.point_menu)
                    setOnMenuItemClickListener {item ->
                        val point = getItem(holder.adapterPosition)
                        when(item.itemId) {
                            R.id.delete -> {
                                listener.onDelete(point)
                                true
                            }
                            R.id.edit -> {
                                listener.onEdit(point)
                                true
                            }
                            else -> false
                        }
                    }
                    show()
                }
            }
        }
        return holder

    }

    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}

class PointDiffCallback : DiffUtil.ItemCallback<Point>() {
    override fun areItemsTheSame(oldItem: Point, newItem: Point): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Point, newItem: Point): Boolean {
        return oldItem == newItem
    }

}