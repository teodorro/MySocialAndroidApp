package com.example.mysocialandroidapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mysocialandroidapp.dao.*
import com.example.mysocialandroidapp.entity.*

@Database(
    entities = [
        PostEntity::class, PostRemoteKeyEntity::class, PostWorkEntity::class,
        UserEntity::class,
        JobEntity::class, JobWorkEntity::class,
        EventEntity::class, EventRemoteKeyEntity::class, EventWorkEntity::class,
    ],
    version = 2, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun postWorkDao(): PostWorkDao
    abstract fun userDao(): UserDao
    abstract fun jobDao(): JobDao
    abstract fun jobWorkDao(): JobWorkDao
    abstract fun eventDao(): EventDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
    abstract fun eventWorkDao(): EventWorkDao
}