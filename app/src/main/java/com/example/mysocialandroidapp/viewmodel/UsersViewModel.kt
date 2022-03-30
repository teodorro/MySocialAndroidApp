package com.example.mysocialandroidapp.viewmodel

import androidx.lifecycle.*
import com.example.mysocialandroidapp.auth.AppAuth
import com.example.mysocialandroidapp.model.UsersFeedModel
import com.example.mysocialandroidapp.repository.UsersRepository
import com.example.mysocialandroidapp.samples.Samples
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UsersRepository,
    val appAuth: AppAuth,
) : ViewModel() {

    var userIds: Set<Long> = emptySet()

    val data: LiveData<UsersFeedModel> = repository.data
        .map { users ->
            UsersFeedModel(
                users.filter { x -> userIds.contains(x.id) },
                users.isEmpty()
            )
        }.asLiveData()

    init {
        loadUsers()
    }

    fun loadUsers() = viewModelScope.launch {
        try {
            repository.getUsers()
        } catch (e: Exception) {
        }
    }

//    private val _usersFeed = MutableLiveData<UsersFeedModel>()
//    val usersFeed: LiveData<UsersFeedModel>
//        get() = _usersFeed
//
//    fun setUsers(userIds: Set<Long>){
//        val userList = Samples.getUsers().filter { x -> userIds.contains(x.id) }
//        _usersFeed.value = UsersFeedModel(userList, userList.isEmpty())
//    }
}