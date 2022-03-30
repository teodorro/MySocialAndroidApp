package com.example.mysocialandroidapp.samples

import android.content.Context
import com.example.mysocialandroidapp.api.DataApiService
import com.example.mysocialandroidapp.auth.AppAuth
import com.example.mysocialandroidapp.auth.AuthState
import com.example.mysocialandroidapp.dto.Post
import com.example.mysocialandroidapp.error.ApiError
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import retrofit2.Response
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Samples2 @Inject constructor(
    private val apiService: DataApiService,
    @ApplicationContext private val context: Context,
) {
    @Inject
    lateinit var appAuth: AppAuth

//    private val _signedInEvent = SingleLiveEvent<Unit>()
//    val signedInEvent: LiveData<Unit>
//        get() = _signedInEvent
//
//    init {
//        signedInEvent.observe(this){
//
//        }
//    }


    suspend fun loadSampleData2() {
        var user = "Jack Sparrow"
        signUpUser("sparrow", "sparrow", user)
        Thread.sleep(500)
        addPosts1(user)
        signOut()

        user = "Captain Barbossa"
        signUpUser("barbossa", "barbossa", user)
        Thread.sleep(500)
        addPosts2(user)
//        signOut()
    }

    private suspend fun addPosts2(user: String) {
        apiService.savePost(
            Post(
                0,
                2,
                user,
                "",
                "You know, the problem with being the last of anything, by and by there will be none left at all.",
                Instant.now().toString(),
                false,
                null,
                null,
                mutableSetOf(),
                false,
                emptySet(),
                null
            )
        )
        apiService.savePost(
            Post(
                0,
                2,
                user,
                "",
                "Deceitful and black-hearted, perhaps we are. But we would never go against the Code. Well, perhaps for good reasons. But mostly never.",
                Instant.now().toString(),
                false,
                null,
                null,
                mutableSetOf(),
                false,
                emptySet(),
                null
            )
        )
    }

    private suspend fun addPosts1(user: String) {
        apiService.savePost(
            Post(
                0,
                1,
                user,
                "",
                "This is the day you will always remember as the day you almost caught Captain Jack Sparrow.",
                Instant.now().toString(),
                false,
                null,
                null,
                mutableSetOf(),
                false,
                emptySet(),
                null
            )
        )

        apiService.savePost(
            Post(
                0,
                1,
                user,
                "",
                "The problem is not the problem. The problem is your attitude about the problem.",
                Instant.now().toString(),
                false,
                null,
                null,
                mutableSetOf(),
                false,
                emptySet(),
                null
            )
        )

        apiService.savePost(
            Post(
                0,
                1,
                user,
                "",
                "Well, then, I confess, it is my intention to commandeer one of these ships, pick up a crew in Tortuga, raid, pillage, plunder and otherwise pilfer my weasely black guts out.",
                Instant.now().toString(),
                false,
                null,
                null,
                mutableSetOf(),
                false,
                emptySet(),
                null
            )
        )

        apiService.savePost(
            Post(
                0,
                1,
                user,
                "",
                "When you marooned me on that godforsaken spit of land, you forgot one very important thing, mate. I'm Captain Jack Sparrow.",
                Instant.now().toString(),
                true,
                null,
                null,
                mutableSetOf(1),
                true,
                setOf(1),
                null
            )
        )
    }

    suspend fun signUpUser(login: String, password: String, name: String) {
        coroutineScope {
            try {
                var response: Response<AuthState> = apiService.signUp(login, password, name)

                if (response.isSuccessful) {
                    signIn(login, password)
                } else
                    throw ApiError(response.code(), response.message())
            } catch (e: Exception) {
                throw ApiError(e.hashCode(), e.message.toString())
            }
        }
    }

    suspend fun signIn(login: String, password: String) {
        CoroutineScope(Dispatchers.Default).launch {
            appAuth.removeAuth() // to refresh data for repeating unsuccessful login. Otherwise no toast.message
            val responseSignIn = apiService.signIn(login, password)
            if (!responseSignIn.isSuccessful) {
                appAuth.setAuth(0, "")
                return@launch
            }
            val body = responseSignIn.body() ?: throw ApiError(
                responseSignIn.code(),
                responseSignIn.message()
            )
            appAuth.setAuth(body.id, body.token ?: "")

            initUser(body.id)
        }
    }

    suspend fun initUser(userId: Long) =
        CoroutineScope(Dispatchers.Default).launch {
            if (userId == 0L) {
                appAuth.setUser(0, "", "", null, emptyList())
                return@launch
            }
            val responseUser = apiService.getUserById(userId)
            if (responseUser.isSuccessful) {
                val body = responseUser.body() ?: throw ApiError(
                    responseUser.code(),
                    responseUser.message()
                )
                appAuth.setUser(body.id, body.login, body.name, body.avatar, body.authorities)
            } else {
                appAuth.setUser(0, "", "", null, emptyList())
            }
        }

    suspend fun signOut() {
        appAuth.removeAuth()
        initUser(0)
    }


    suspend fun loadSampleData() {
        coroutineScope {
            apiService.signUp("sparrow", "sparrow", "Jack Sparrow")
//            val responseSignIn = apiService.signIn("sparrow", "sparrow")
//            val body = responseSignIn.body() ?: throw ApiError(responseSignIn.code(), responseSignIn.message())
//            val pushToken = PushToken(body.token ?: Firebase.messaging.token.await())
//            apiService.save(pushToken)


            apiService.savePost(
                Post(
                    0,
                    1,
                    "Jack Sparrow",
                    "",
                    "This is the day you will always remember as the day you almost caught Captain Jack Sparrow.",
                    Instant.now().toString(),
                    false,
                    null,
                    null,
                    mutableSetOf(),
                    false,
                    emptySet(),
                    null
                )
            )

            apiService.savePost(
                Post(
                    0,
                    1,
                    "Jack Sparrow",
                    "",
                    "The problem is not the problem. The problem is your attitude about the problem.",
                    Instant.now().toString(),
                    false,
                    null,
                    null,
                    mutableSetOf(),
                    false,
                    emptySet(),
                    null
                )
            )
        }
    }
}