package com.example.mysocialandroidapp.model

data class JobsFeedModelState (
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
)