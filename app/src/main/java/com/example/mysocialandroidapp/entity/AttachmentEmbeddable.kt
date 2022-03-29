package com.example.mysocialandroidapp.entity

import com.example.mysocialandroidapp.dto.Attachment
import com.example.mysocialandroidapp.enumeration.AttachmentType

data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType,
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}