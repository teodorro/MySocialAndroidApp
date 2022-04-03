package com.example.mysocialandroidapp.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class PostsRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPostRepository(impl: PostsRepositoryImpl): PostsRepository
}