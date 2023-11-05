package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String? = null,
    val content: String? = null,
    val published: String? = null,
    val likes: Int = 0,
    val likedByMe: Boolean = false,
    val share: Int = 0,
    val views: Int = 0,
    val videoLink: String? = null,
    val saved: Boolean = false,
    val authorAvatar: String? = null,
    val attachment: Attachment? = null
)

data class Attachment(
    val url: String? = null,
    val description: String? = null,
    val type: String? = null
)