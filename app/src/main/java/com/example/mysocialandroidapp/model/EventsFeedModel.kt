package com.example.mysocialandroidapp.model

import com.example.mysocialandroidapp.dto.Event

data class EventsFeedModel (
    val events: List<Event> = emptyList(),
    val empty: Boolean = false,
)