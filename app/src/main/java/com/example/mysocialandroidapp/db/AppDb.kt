package com.example.mysocialandroidapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mysocialandroidapp.dao.*
import com.example.mysocialandroidapp.entity.PostEntity
import com.example.mysocialandroidapp.entity.PostRemoteKeyEntity
import com.example.mysocialandroidapp.entity.PostWorkEntity
import com.example.mysocialandroidapp.entity.UserEntity

@Database(entities = [
    PostEntity::class, PostRemoteKeyEntity::class, PostWorkEntity::class,
    UserEntity::class],
    version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun postWorkDao(): PostWorkDao
    abstract fun userDao(): UserDao
}