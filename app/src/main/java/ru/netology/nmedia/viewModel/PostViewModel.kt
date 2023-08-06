package ru.netology.nmedia.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

private val defaultPost = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = ""
)

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll() // св-во data переадресовываем в репозиторий

    val edited = MutableLiveData(defaultPost)

    fun likeById(id: Long) =
        repository.likeById(id) // ф-цию like у viewModel тоже переадресовываем в репозиторий

    fun shareById(id: Long) = repository.shareById(id)
    fun viewById(id: Long) = repository.viewById(id)
    fun removeById(id: Long) = repository.removeById(id)

//    fun save () {
//        edited.value?.let {
//            repository.save(it)
//        }
//        edited.value = defaultPost
//    }

//    fun changeContent(content: String) {
//        val text = content.trim()
//        if (edited.value?.content == text) {
//            return
//        }
//        edited.value = edited.value?.copy(content = text)
//    }

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
        edited.value = post
    }

    fun cancel() {
        edited.value = defaultPost
    }


}