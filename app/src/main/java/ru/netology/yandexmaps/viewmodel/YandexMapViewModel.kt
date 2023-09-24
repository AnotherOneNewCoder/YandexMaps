package ru.netology.yandexmaps.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.yandexmaps.db.PointDB
import ru.netology.yandexmaps.dto.Point
import ru.netology.yandexmaps.entity.PointEntity

class YandexMapViewModel(application: Application): AndroidViewModel(application) {

    private val dao = PointDB.getInstance(application).pointDao

    val points = dao.getAll().map {
        it.map(PointEntity::toDto)
    }

    suspend fun deleteById(id: Long) {
        viewModelScope.launch {
            dao.deleteById(id)
        }
    }
    suspend fun addPoint(point: Point) {
        viewModelScope.launch{
            dao.insert(PointEntity.fromDto(point))
        }
    }
}