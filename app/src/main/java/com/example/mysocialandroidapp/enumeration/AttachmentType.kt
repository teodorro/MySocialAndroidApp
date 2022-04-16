package com.example.mysocialandroidapp.enumeration

enum class AttachmentType(val value: Int) {
    IMAGE(1),
    AUDIO(2),
    OTHER(3);

    companion object {
        fun fromInt(value: Int) = AttachmentType.values().first { it.value == value }
    }
}