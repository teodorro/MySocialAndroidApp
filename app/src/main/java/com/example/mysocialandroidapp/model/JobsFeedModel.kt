package com.example.mysocialandroidapp.model

import com.example.mysocialandroidapp.dto.Job

data class JobsFeedModel(
    val jobs: List<Job> = emptyList(),
    val empty: Boolean = false,
)