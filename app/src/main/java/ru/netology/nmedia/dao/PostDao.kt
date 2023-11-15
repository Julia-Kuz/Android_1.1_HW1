package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

//    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
//    suspend fun isEmpty(): Boolean

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)


    @Query("DELETE FROM PostEntity WHERE saved = 0 AND content = :content")
    suspend fun removePost(content: String)

    @Transaction
    suspend fun updatePost (post: PostEntity) {
        post.content?.let { removePost(it) }
        insert(post.copy(saved = true))
    }

//    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
//    suspend fun updateContentById(id: Long, content: String)

//    suspend fun save (post: PostEntity) =
//        if (post.id == 0L) insert(post) else post.content?.let { updateContentById(post.id, it) }

    @Query("""
        UPDATE PostEntity SET
        likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """)
    suspend fun likeById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("UPDATE PostEntity SET share = share + 1 WHERE id = :id")
    fun shareById(id: Long)

    @Query("UPDATE PostEntity SET views = views + 1 WHERE id = :id")
    fun viewById(id: Long)

    @Query("UPDATE PostEntity SET videoLink = :link WHERE id = :id")
    fun addLink(id: Long, link: String)
}