package com.example.mysocialandroidapp.viewmodel

import androidx.lifecycle.*
import com.example.mysocialandroidapp.auth.AppAuth
import com.example.mysocialandroidapp.model.PostFeedModelState
import com.example.mysocialandroidapp.model.UsersFeedModel
import com.example.mysocialandroidapp.model.UsersFeedModelState
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

    private val _dataState = MutableLiveData<UsersFeedModelState>()
    val dataState: LiveData<UsersFeedModelState>
        get() = _dataState

    init {
        loadUsers()
    }

    fun loadUsers() = viewModelScope.launch {
        try {
            repository.getUsers()
        } catch (e: Exception) {
        }
    }

    fun clearLocalTable(){
        viewModelScope.launch {
            try {
                _dataState.value = UsersFeedModelState(loading = true)
                repository.clearLocalTable()
                _dataState.value = UsersFeedModelState()
            } catch (e: Exception) {
                _dataState.value = UsersFeedModelState(error = true)
            }
        }
    }
}