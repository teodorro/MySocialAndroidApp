package com.example.mysocialandroidapp.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.mysocialandroidapp.api.DataApiService
import com.example.mysocialandroidapp.dao.PostDao
import com.example.mysocialandroidapp.dao.PostRemoteKeyDao
import com.example.mysocialandroidapp.db.AppDb
import com.example.mysocialandroidapp.entity.PostEntity
import com.example.mysocialandroidapp.entity.PostRemoteKeyEntity
import com.example.mysocialandroidapp.entity.toEntity
import com.example.mysocialandroidapp.error.ApiError

@OptIn(ExperimentalPagingApi::class)
class WallRemoteMediator(
    private val service: DataApiService,
    private val db: AppDb,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val userId: Long
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    if (state.firstItemOrNull() != null) {
                        service.getWallPosts(userId)
                    } else
                        service.getWallPostsLatest(userId, state.config.initialLoadSize)
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(
                response.code(),
                response.message(),
            )

            db.withTransaction {
                when (loadType){
                    LoadType.REFRESH -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                type = PostRemoteKeyEntity.KeyType.AFTER,
                                id = body.first().id,
                            )
                        )
                        postDao.insert(body.toEntity(false))
                    }
                    LoadType.APPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                type = PostRemoteKeyEntity.KeyType.BEFORE,
                                id = body.last().id,
                            )
                        )
                        postDao.insert(body.toEntity(false))
                    }
                }
            }
            return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}