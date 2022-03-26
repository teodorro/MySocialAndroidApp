package com.example.mysocialandroidapp.auth

object AppAuth {
    var currentAuthorId: Long = 1001
    var currentAuthorName: String = "author1"

    fun setAuth(id: Long, name: String){
        currentAuthorId = id
        currentAuthorName = name
    }
}