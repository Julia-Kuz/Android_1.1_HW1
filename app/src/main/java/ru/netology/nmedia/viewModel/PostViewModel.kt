package ru.netology.nmedia.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryFileImpl
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl
import ru.netology.nmedia.repository.PostRepositorySharedPreferencesImpl

private val defaultPost = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = ""
)

//class PostViewModel : ViewModel() {
//    private val repository: PostRepository = PostRepositoryInMemoryImpl()

class PostViewModel (application: Application) : AndroidViewModel (application) {
    //private val repository: PostRepository = PostRepositorySharedPreferencesImpl(application) // здесь нужен context (переменная application), чтобы его получить, нужно пробросить через конструктор (строка 24)
    private val repository: PostRepository = PostRepositoryFileImpl(application)

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
        edited.value = post.copy()
        edited.value?.let {
            repository.save(it.copy())
        }
        edited.value = defaultPost
    }

//    fun cancel() {
//        edited.value = defaultPost
//    }


}