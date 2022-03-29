package com.example.mysocialandroidapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mysocialandroidapp.dao.Converters
import com.example.mysocialandroidapp.dao.PostDao
import com.example.mysocialandroidapp.dao.PostRemoteKeyDao
import com.example.mysocialandroidapp.dao.PostWorkDao
import com.example.mysocialandroidapp.entity.PostEntity
import com.example.mysocialandroidapp.entity.PostRemoteKeyEntity
import com.example.mysocialandroidapp.entity.PostWorkEntity

@Database(entities = [
    PostEntity::class, PostRemoteKeyEntity::class, PostWorkEntity::class],
    version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun postWorkDao(): PostWorkDao
}