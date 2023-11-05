package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity (tableName = "PostEntity") //по умолчанию имя таблицы = названию класса, при желании можно изменить здесь, в кругых скобках
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String?,
    val content: String?,
    val published: String?,
    val likes: Int,
    val likedByMe: Boolean,
    val share: Int,
    val views: Int,
    val videoLink: String?,
    val saved: Boolean

) {
    fun toDto() = Post(id, author, content, published, likes, likedByMe, share, views, videoLink, saved)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.content, published = SimpleDateFormat("dd MMM yyyy в HH:mm", Locale.getDefault()).format(Date()).toString(), dto.likes, dto.likedByMe, dto.share, dto.views, dto.videoLink, dto.saved)

    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)