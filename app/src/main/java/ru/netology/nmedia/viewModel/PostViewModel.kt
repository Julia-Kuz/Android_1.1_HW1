package ru.netology.nmedia.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryRoomImpl

private val defaultPost = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = "",
    videoLink = null
)

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository = PostRepositoryRoomImpl(AppDb.getInstance(context = application).postDao()) // Room

    val data = repository.getAll()

    val edited = MutableLiveData(defaultPost)

    var link = null

   // var draft: String = ""   // для черновика

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun viewById(id: Long) = repository.viewById(id)
    fun removeById(id: Long) = repository.removeById(id)
    fun addLink(id: Long, link: String) = repository.addLink(id, link)

    fun changeContentAndSave(content: String) {
        edited.value?.let {
            val text = content.trim()
            if (text != it.content) {
                repository.save(it.copy(content = text))
            }
        }
        edited.value = defaultPost
    }

    fun edit(post: Post) {
        edited.value = post.copy()
        edited.value?.let {
            repository.save(it.copy())
        }
        edited.value = defaultPost
    }

}