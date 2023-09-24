package ru.netology.yandexmaps.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.netology.yandexmaps.dao.PointDao
import ru.netology.yandexmaps.entity.PointEntity


@Database(entities = [PointEntity::class], version = 1)
abstract class PointDB : RoomDatabase() {
    abstract val pointDao: PointDao

    companion object {
        @Volatile
        private var INSTANCE: PointDB? = null

        fun getInstance(context: Context): PointDB =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(context, PointDB::class.java, "point_db")
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
    }


}