package ru.netology.yandexmaps.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.yandexmaps.dto.Point


@Entity
data class PointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val lat: Double,
    val long: Double,
    val title: String,

) {
    companion object {
        fun fromDto(point: Point) : PointEntity = with(point) {
            PointEntity(id = id, lat = lat, long = long, title = title)
        }
    }

    fun toDto(): Point = Point(id = id, lat = lat, long = long, title = title)
}