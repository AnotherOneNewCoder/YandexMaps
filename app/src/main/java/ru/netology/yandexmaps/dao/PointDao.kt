package ru.netology.yandexmaps.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.yandexmaps.entity.PointEntity

@Dao
interface PointDao {
    @Query("SELECT * FROM Pointentity")
    fun getAll(): Flow<List<PointEntity>>

    @Query("DELETE FROM Pointentity WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(point: PointEntity)
}