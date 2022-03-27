package com.example.mysocialandroidapp.auth

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mysocialandroidapp.api.DataApiService
import com.example.mysocialandroidapp.dto.PushToken
import com.example.mysocialandroidapp.dto.User
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

//region
//object AppAuth {
//    var currentAuthorId: Long = 1001
//    var currentAuthorName: String = "author1"
//
//    fun setAuth(id: Long, name: String){
//        currentAuthorId = id
//        currentAuthorName = name
//    }
//}
//endregion

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext private val context: Context,
) {
//    var authStateFlow: LiveData<AuthState> = MutableLiveData(AuthState(123, "asd"))
    private val prefs = context.getSharedPreferences(
        "auth",
        Context.MODE_PRIVATE)
    private val idKey = "id"
    private val tokenKey = "token"
    private val _authStateFlow: MutableStateFlow<AuthState>
    private val _userFlow: MutableStateFlow<User>

    // if saved login&pass then sign in
    init{
        val id = prefs.getLong(idKey, 0)
        val token = prefs.getString(tokenKey, null)

        if (id == 0L || token == null){
            _authStateFlow = MutableStateFlow(AuthState())
            with(prefs.edit()){
                clear()
                apply()
            }
        } else{
            _authStateFlow = MutableStateFlow(AuthState(id, token))
        }
        _userFlow = MutableStateFlow(User(0, "", "", null, emptyList()))
    }

    val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()
    val userFlow: StateFlow<User> = _userFlow.asStateFlow()

    @Synchronized
    fun setAuth(id: Long, token: String){
        _authStateFlow.value = AuthState(id, token)
        with(prefs.edit()){
            putLong(idKey, id)
            putString(tokenKey, token)
            apply()
        }
        sendPushToken()
    }

    @Synchronized
    fun removeAuth(){
        _authStateFlow.value = AuthState()
        with(prefs.edit()){
            clear()
            commit()
        }
        sendPushToken()
    }

    fun setUser(id: Long, login: String, name: String, avatar: Any?, authorities: List<String>){
        _userFlow.value = User(id, login, name, avatar, authorities)
    }


    //region ApiService artificial EntryPoint

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun apiService(): DataApiService
    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val pushToken = PushToken(token ?: Firebase.messaging.token.await())
                getApiService(context).save(pushToken)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getApiService(context: Context): DataApiService {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context,
            AppAuthEntryPoint::class.java
        )
        return hiltEntryPoint.apiService()
    }

    //endregion

}