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
class PostsRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val postWorkDao: PostWorkDao,
    private val appDb: AppDb,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val apiService: DataApiService,
    private val userDao: UserDao,
) : PostsRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val allPosts: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        remoteMediator = PostsRemoteMediator(apiService, appDb, postDao, postRemoteKeyDao),
        pagingSourceFactory = postDao::pagingSource,
    ).flow.map { pagingData ->
        pagingData.map(PostEntity::toDto)
    }

    @OptIn(ExperimentalPagingApi::class)
    override var userId: Long = 0
        set(value) {
            field = value
            userPosts = Pager(
                config = PagingConfig(pageSize = 5, enablePlaceholders = false),
                remoteMediator = WallRemoteMediator(apiService, appDb, postDao, postRemoteKeyDao, value),
                pagingSourceFactory = postDao::pagingSource,
            ).flow.map { pagingData ->
                pagingData.map(PostEntity::toDto)
            }
        }

    @OptIn(ExperimentalPagingApi::class)
    override var userPosts: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
        remoteMediator = WallRemoteMediator(apiService, appDb, postDao, postRemoteKeyDao, userId),
        pagingSourceFactory = postDao::pagingSource,
    ).flow.map { pagingData ->
        pagingData.map(PostEntity::toDto)
    }

    override val allUsers = userDao.getAll()
        .map(List<UserEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun getUsers() {
        try {
            // получить всех пользователей с сервера
            val response = apiService.getUsersAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            // обновить базу. Новые добавить, несовпадающие заменить.
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            userDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

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

    override suspend fun getAllPosts() {
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

    override suspend fun getUserPosts(userId: Long) {
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

    override suspend fun updateWasSeen() {
        try {
            postDao.updateWasSeen()
        } catch (e: Exception) {
            throw UnknownError
        }
    }

//    override suspend fun removeById(postId: Long) {
//        try {
//            val response = apiService.removePostById(postId)
//            if (!response.isSuccessful) {
//                throw ApiError(response.code(), response.message())
//            }
//
//            postDao.removeById(postId)
//        } catch (e: IOException) {
//            throw NetworkError
//        } catch (e: Exception) {
//            throw UnknownError
//        }
//    }

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
            val response = apiService.savePost(post)
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