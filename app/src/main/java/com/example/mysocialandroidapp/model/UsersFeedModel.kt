package com.example.mysocialandroidapp.model

import com.example.mysocialandroidapp.dto.User

data class UsersFeedModel(
    val users: List<User> = emptyList(),
    val empty: Boolean = false,
)