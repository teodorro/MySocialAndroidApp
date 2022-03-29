package com.example.mysocialandroidapp.repository

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.paging.*
import com.example.mysocialandroidapp.api.DataApiService
import com.example.mysocialandroidapp.dao.*
import com.example.mysocialandroidapp.db.AppDb
import com.example.mysocialandroidapp.dto.*
import com.example.mysocialandroidapp.entity.*
import com.example.mysocialandroidapp.enumeration.AttachmentType
import com.example.mysocialandroidapp.error.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val postWorkDao: PostWorkDao,
    appDb: AppDb,
    postRemoteKeyDao: PostRemoteKeyDao,
    private val apiService: DataApiService,
//    private val userDao: UserDao,
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        remoteMediator = PostRemoteMediator(apiService, appDb, postDao, postRemoteKeyDao),
        pagingSourceFactory = postDao::pagingSource,
    ).flow.map { pagingData ->
        pagingData.map(PostEntity::toDto)
    }

//    override val allUsers = userDao.getAll()
//        .map(List<UserEntity>::toDto)
//        .flowOn(Dispatchers.Default)
//
//    override suspend fun getUsers() {
//        try {
//            // получить всех пользователей с сервера
//            val response = apiService.getUsersAll()
//            if (!response.isSuccessful) {
//                throw ApiError(response.code(), response.message())
//            }
//
//            // обновить базу. Новые добавить, несовпадающие заменить.
//            val body = response.body() ?: throw ApiError(response.code(), response.message())
//            userDao.insert(body.toEntity())
//        } catch (e: IOException) {
//            throw NetworkError
//        } catch (e: Exception) {
//            throw UnknownError
//        }
//    }

    override fun getNewerCount(postId: Long): Flow<Int> = flow {
        while (true) {
            delay(1_000)

            val response = apiService.getPostsNewer(postId)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(body.toEntity(false))
            emit(body.size)
        }
    }.catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun getAll() {
        try {
            // получить все посты с сервера
            val response = apiService.getPostsAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            // обновить базу. Новые добавить, несовпадающие заменить.
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(body.toEntity(false))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(postId: Long) {
        try {
            val response = apiService.removePostById(postId)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            postDao.removeById(postId)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(postId: Long) {
        try {
            val postResponse = apiService.getPostById(postId)
            val postBody =
                postResponse.body() ?: throw ApiError(postResponse.code(), postResponse.message())

            val response: Response<Post> = if (!postBody.likedByMe) {
                apiService.likeById(postId)
            } else {
                apiService.dislikeById(postId)
            }
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body, true)) //TODO: wasSeen = false
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun updateWasSeen() {
        try {
            postDao.updateWasSeen()
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeWork(postId: Long) {
        try {
            val response = apiService.removePostById(postId)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            postDao.removeById(postId)

        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun clearLocalTable() {
        try{
            postDao.removeAll()
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}