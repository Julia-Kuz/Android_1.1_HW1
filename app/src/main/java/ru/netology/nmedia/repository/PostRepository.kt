package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Long, flag: Boolean): Post
    fun shareById(id: Long)
    fun viewById(id: Long)
    fun removeById(id: Long)
    fun save(post: Post)
    fun addLink (id: Long, link: String)

    fun getAllAsync(callback: GetMyCallback <List <Post>>)
    fun likeByIdAsync (id: Long, flag: Boolean, callback: GetMyCallback <Post>)
    fun removeByIdAsync(id: Long, callback: GetMyCallback <Unit>)
    fun saveAsync(post: Post, callback: GetMyCallback <Post>)

    interface GetMyCallback <T> {
        fun onSuccess(result: T) {}
        fun onError(e: Exception) {}
    }

//пример до generic - передавался в  getAllAsync: fun getAllAsync(callback: GetAllCallback)
//    interface  {
//        fun onSuccess(posts: List<Post>) //{}
//        fun onError(e: Exception) //{}
//    }

}