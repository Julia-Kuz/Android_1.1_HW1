package ru.netology.nmedia.dto

import ru.netology.nmedia.util.AttachmentType

sealed interface FeedItem{
    val id: Long
}

data class Ad(
    override val id: Long,
    //val url: String,
    val image: String,
) : FeedItem

data class TimingSeparator (
    override val id: Long = 0L,
    val name: String
): FeedItem

data class Post(
    override val id: Long,
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
) : FeedItem

data class Attachment(
    val url: String = "",
    val description: String? = null,
    val type: AttachmentType
)