package com.example.mysocialandroidapp.model

data class PostFeedModelState (
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
)