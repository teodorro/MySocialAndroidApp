package com.example.mysocialandroidapp.model

import com.example.mysocialandroidapp.dto.Post

data class PostsFeedModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false,
)