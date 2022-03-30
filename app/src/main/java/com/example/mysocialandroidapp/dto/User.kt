package com.example.mysocialandroidapp.dto

data class User (
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String?,
    val authorities: List<String>,
)