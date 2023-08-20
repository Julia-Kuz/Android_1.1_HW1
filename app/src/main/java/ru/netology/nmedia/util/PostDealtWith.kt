package ru.netology.nmedia.util

import ru.netology.nmedia.dto.Post

object PostDealtWith {
    private lateinit var postDealtWith: Post

    fun get() = postDealtWith

    fun savePostDealtWith (post: Post) {
       postDealtWith = post
    }
}