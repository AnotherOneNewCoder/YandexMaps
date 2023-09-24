package ru.netology.yandexmaps.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.yandexmaps.dto.Point


@Entity
data class PointEntity constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val latitude: Double,
    val longitude: Double,
    val title: String,

) {
    companion object {
        fun fromDto(point: Point) : PointEntity = with(point) {
            PointEntity(id = id, latitude = lat, longitude = long, title = title)
        }
    }

    fun toDto(): Point = Point(id = id, lat = latitude, long = longitude, title = title)
}