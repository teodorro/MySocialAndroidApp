package com.example.mysocialandroidapp.repository

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.paging.*
import com.example.mysocialandroidapp.api.DataApiService
import com.example.mysocialandroidapp.dao.*
import com.example.mysocialandroidapp.db.AppDb
import com.example.mysocialandroidapp.dto.Attachment
import com.example.mysocialandroidapp.dto.Event
import com.example.mysocialandroidapp.dto.Media
import com.example.mysocialandroidapp.dto.MediaUpload
import com.example.mysocialandroidapp.entity.*
import com.example.mysocialandroidapp.enumeration.AttachmentType
import com.example.mysocialandroidapp.error.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class EventsRepositoryImpl @Inject constructor(
    private val eventDao: EventDao,
    private val eventWorkDao: EventWorkDao,
    private val appDb: AppDb,
    eventRemoteKeyDao: EventRemoteKeyDao,
    private val apiService: DataApiService,
    private val userDao: UserDao,
): EventsRepository {

//    @OptIn(ExperimentalPagingApi::class)
//    override val data: Flow<PagingData<Event>> = Pager(
//        config = PagingConfig(pageSize = 5, enablePlaceholders = false),
//        remoteMediator = EventsRemoteMediator(apiService, appDb, eventDao, eventRemoteKeyDao),
//        pagingSourceFactory = eventDao::pagingSource,
//    ).flow.map { pagingData ->
//        pagingData.map(EventEntity::toDto)
//    }


    override val data = eventDao.getAll()
        .map(List<EventEntity>::toDto)
        .flowOn(Dispatchers.Default)

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

    override fun getNewerCount(eventId: Long): Flow<Int> {
        TODO("Not yet implemented")
    }

    override suspend fun getAll() {
        try {
            // получить все события с сервера
            val response = apiService.getEvents()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            // обновить базу. Новые добавить, несовпадающие заменить.
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(event: Event) {
        try {
            val response = apiService.saveEvent(event)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(eventId: Long) {
        try {
            val response = apiService.removeEventById(eventId)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            eventDao.removeById(eventId)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(eventId: Long) {
        try {
            val eventResponse = apiService.getEventById(eventId)
            val eventBody =
                eventResponse.body() ?: throw ApiError(eventResponse.code(), eventResponse.message())

            val response: Response<Event> = if (!eventBody.likedByMe) {
                apiService.likeEventById(eventId)
            } else {
                apiService.dislikeEventById(eventId)
            }
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun participateEventById(eventId: Long) {
        try {
            val eventResponse = apiService.getEventById(eventId)
            val eventBody =
                eventResponse.body() ?: throw ApiError(eventResponse.code(), eventResponse.message())

            val response: Response<Event> = if (!eventBody.participatedByMe) {
                apiService.participateEventById(eventId)
            } else {
                apiService.withdrawEventById(eventId)
            }
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


    override suspend fun saveWithAttachment(event: Event, upload: MediaUpload) {
        try {
            val media = upload(upload)
            // TODO: add support for other types
            val eventWithAttachment =
                event.copy(attachment = Attachment(media.url, AttachmentType.IMAGE))
            save(eventWithAttachment)
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

    override suspend fun saveWork(event: Event, upload: MediaUpload?): Long {
        try {
            val entity = EventWorkEntity.fromDto(event).apply {
                if (upload != null) {
                    this.link = upload.file.toUri().toString()
                }
            }
            var a = eventWorkDao.insert(entity)
            return a
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun processWork(eventId: Long) {
        try {
            val entity = eventWorkDao.getById(eventId)
            var event = entity.toDto()
            if (entity.link != null) {
                val upload = MediaUpload(Uri.parse(entity.link).toFile())
                val media = upload(upload)
                event = event.copy(attachment = Attachment(media.url, AttachmentType.IMAGE))
            }
            save(event)

            Log.d(null, entity.id.toString())
            Log.d(null, event.id.toString())
        } catch (e: Exception) {
            var a = e.message
            throw UnknownError
        }
    }

    override suspend fun removeWork(eventId: Long) {
        try {
            val response = apiService.removeEventById(eventId)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            eventDao.removeById(eventId)

        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun clearLocalTable() {
        try{
            eventDao.removeAll()
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}