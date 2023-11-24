package ru.netology.nmedia.dto

import ru.netology.nmedia.util.AttachmentType

data class Post(
    val id: Long,
    val author: String,
    val authorId: Long,
    val content: String,
    val published: Long,
    val likes: Int = 0,
    val likedByMe: Boolean = false,
    val share: Int = 0,
    val views: Int = 0,
    val videoLink: String? = null,
    val saved: Boolean = false,
    val authorAvatar: String = "",
    val hidden: Boolean,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false
)

data class Attachment(
    val url: String = "",
    val description: String? = null,
    val type: AttachmentType
)