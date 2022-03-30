package com.example.mysocialandroidapp.dao

import com.example.mysocialandroidapp.db.AppDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class DaoModule {
    @Provides
    fun providePostDao(db: AppDb): PostDao = db.postDao()

    @Provides
    fun providePostRemoteKeyDao(db: AppDb): PostRemoteKeyDao = db.postRemoteKeyDao()

    @Provides
    fun providePostWorkDao(db: AppDb): PostWorkDao = db.postWorkDao()

    @Provides
    fun provideUserDao(db: AppDb): UserDao = db.userDao()
}