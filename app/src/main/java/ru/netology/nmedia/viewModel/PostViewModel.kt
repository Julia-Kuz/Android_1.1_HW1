package ru.netology.nmedia.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.IOException
import kotlin.concurrent.thread

private val defaultPost = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = "",
    videoLink = null
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    //private val repository: PostRepository = PostRepositoryRoomImpl(AppDb.getInstance(context = application).postDao()) // Room

    private val repository: PostRepository = PostRepositoryImpl()

    private val _data = MutableLiveData(FeedModelState())
    val data: LiveData<FeedModelState> = _data

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit> = _postCreated

    val edited = MutableLiveData(defaultPost)
    var link = null
    // var draft: String = ""   // для черновика

    init {
        load()
    }

    fun load() {
        //создаем фоновый поток
        thread {
            _data.postValue(FeedModelState(loading = true)) //показываем, что идет загрузка
            try {
                // Данные успешно получены
                val posts = repository.getAll()
                _data.postValue(FeedModelState(posts = posts, empty = posts.isEmpty()))
            } catch (e: IOException) {
                // Получена ошибка
                _data.postValue(FeedModelState(error = true))
            }
            // м.убрать _data.postValue в с.45 и 48 => продолжение кода:
            // .let { _data.postValue(it) } //.also(_data::postValue) или так (одно и то же)

        }
    }

//    fun save() {
//        edited.value?.let {
//            thread {
//                repository.save(it)
//                _postCreated.postValue(Unit)
//                load()
//            }
//        }
//        edited.postValue(defaultPost) // поскольку фоновый поток, используем метод postValue(), а не setValue()
//    }

    fun changeContentAndSave(content: String) {
        thread {
            edited.value?.let {
                val text = content.trim()
                if (text != it.content) {
                    repository.save(it.copy(content = text))
                    _postCreated.postValue(Unit)
                    load()
                }
            }
            edited.postValue(defaultPost) // поскольку фоновый поток, используем метод postValue(), а не setValue()
        }
    }

    fun likeById(id: Long) {
        val flag = data.value?.posts?.find { it.id == id }?.likedByMe
        if (flag != null) {
            thread {
                repository.likeById(id, flag = flag)
                load()
            }
        }
    }

    fun shareById(id: Long) = repository.shareById(id)
    fun viewById(id: Long) = repository.viewById(id)
    fun removeById(id: Long) {
        thread {
            // Оптимистичная модель
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id }
                )
            )
            try {
                repository.removeById(id)
            } catch (e: IOException) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }
    }

    fun addLink(id: Long, link: String) = repository.addLink(id, link)


    fun edit(post: Post) {
        edited.value = post
        thread {
            edited.value?.let {
                repository.save(it.copy())
                _postCreated.postValue(Unit)
                load()
            }
            edited.postValue(defaultPost)
        }
    }


}