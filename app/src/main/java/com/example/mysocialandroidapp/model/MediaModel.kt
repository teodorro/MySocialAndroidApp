package com.example.mysocialandroidapp.model

import android.net.Uri
import com.example.mysocialandroidapp.enumeration.AttachmentType
import java.io.File

data class MediaModel(
    val uri: Uri? = null,
    val file: File? = null,
    val type: AttachmentType = AttachmentType.IMAGE
)