package com.example.mysocialandroidapp.repository

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.paging.*
import com.example.mysocialandroidapp.api.*
import com.example.mysocialandroidapp.dao.*
import com.example.mysocialandroidapp.db.AppDb
import com.example.mysocialandroidapp.dto.*
import com.example.mysocialandroidapp.entity.*
import com.example.mysocialandroidapp.enumeration.AttachmentType
import com.example.mysocialandroidapp.error.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val postWorkDao: PostWorkDao,
    private val appDb: AppDb,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val apiService: DataApiService,
): WallRepository {

    @OptIn(ExperimentalPagingApi::class)
    override var userId: Long = 0
        set(value) {
            field = value
            data = Pager(
                config = PagingConfig(pageSize = 5, enablePlaceholders = false),
                remoteMediator = WallRemoteMediator(apiService, appDb, postDao, postRemoteKeyDao, value),
                pagingSourceFactory = postDao::pagingSource,
            ).flow.map { pagingData ->
                pagingData.map(PostEntity::toDto)
            }
        }

    @OptIn(ExperimentalPagingApi::class)
    override var data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        remoteMediator = WallRemoteMediator(apiService, appDb, postDao, postRemoteKeyDao, userId),
        pagingSourceFactory = postDao::pagingSource,
    ).flow.map { pagingData ->
        pagingData.map(PostEntity::toDto)
    }

    override suspend fun getAllPosts(userId: Long) {
        try {
            // получить все посты с сервера
            val response = apiService.getWallPosts(userId)
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

    override suspend fun likeById(userId: Long, postId: Long) {
        try {
            val postResponse = apiService.getWallPostById(userId, postId)
            val postBody =
                postResponse.body() ?: throw ApiError(postResponse.code(), postResponse.message())

            val response: Response<Post> = if (!postBody.likedByMe) {
                apiService.likeWallPostById(userId, postId)
            } else {
                apiService.dislikeWallPostById(userId, postId)
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

    override suspend fun saveWork(post: Post, upload: MediaUpload?): Long {
        try {
            val entity = PostWorkEntity.fromDto(post).apply {
                if (upload != null) {
                    this.uri = upload.file.toUri().toString()
                }
            }
            return postWorkDao.insert(entity)
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun processWork(postId: Long) {
        try {
            val entity = postWorkDao.getById(postId)
            var post = entity.toDto()
            if (entity.uri != null) {
                val upload = MediaUpload(Uri.parse(entity.uri).toFile())
                val media = upload(upload)
                post = post.copy(attachment = Attachment(media.url, AttachmentType.IMAGE))
            }
            save(post)

            Log.d(null, entity.id.toString())
            Log.d(null, post.id.toString())
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = apiService.savePosts(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body, true)) //TODO: wasSeen == false
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
        try {
            val media = upload(upload)
            // TODO: add support for other types
            val postWithAttachment =
                post.copy(attachment = Attachment(media.url, AttachmentType.IMAGE))
            save(postWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: java.io.IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun upload(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )

            val response = apiService.uploadMedia(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: java.io.IOException) {
            throw NetworkError
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