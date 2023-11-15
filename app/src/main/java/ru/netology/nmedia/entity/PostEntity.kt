package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AttachmentType

@Entity (tableName = "PostEntity") //по умолчанию имя таблицы = названию класса, при желании можно изменить здесь, в кругых скобках
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published: Long,
    val likes: Int,
    val likedByMe: Boolean,
    val share: Int,
    val views: Int,
    val videoLink: String?,
    val saved: Boolean,
    val authorAvatar: String,
    val attachmentUrl: String?,
    val attachmentDescription: String?,
    val attachmentType: AttachmentType,
    val hidden: Boolean

) {
    fun toDto() = Post(id, author, content, published, likes, likedByMe, share, views, videoLink, saved, authorAvatar, attachmentToDto(), hidden)

    private fun attachmentToDto () : Attachment {
        return Attachment(attachmentUrl, attachmentDescription, attachmentType)
    }

    companion object {
        fun fromDto(dto: Post) : PostEntity {
            val attachmentDto = dto.attachmentFromDTO()
            return if (attachmentDto != null) {
                PostEntity(dto.id, dto.author, dto.content, dto.published, dto.likes, dto.likedByMe, dto.share, dto.views, dto.videoLink, dto.saved, dto.authorAvatar,
                    attachmentDto.url, attachmentDto.description, attachmentDto.type, dto.hidden)
            } else {
                PostEntity(dto.id, dto.author, dto.content, dto.published, dto.likes, dto.likedByMe, dto.share, dto.views, dto.videoLink, dto.saved, dto.authorAvatar,
                    attachmentUrl = null, attachmentDescription = null, attachmentType = AttachmentType.NONE, dto.hidden)
            }
        }

    }
}

data class AttachmentEntity(
    val url: String? = null,
    val description: String? = null,
    val type: AttachmentType
)


fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)