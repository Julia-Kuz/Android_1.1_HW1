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

    private val _postCreatedError = SingleLiveEvent<Unit>()
    val postCreatedError: LiveData<Unit> = _postCreatedError

    val edited = MutableLiveData(defaultPost)
    var link = null
    // var draft: String = ""   // для черновика

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.postValue(FeedModelState(loading = true)) //показываем, что идет загрузка
        repository.getAllAsync(object : PostRepository.GetMyCallback<List<Post>> {
            override fun onSuccess(result: List<Post>) {
               // _data.postValue(FeedModelState(posts = result, empty = result.isEmpty())) // Okhttp, фоновый поток
                _data.value = FeedModelState(posts = result, empty = result.isEmpty()) //retrofit, теперь с главного потока
            }
            override fun onError(e: Exception) {
               // _data.postValue(FeedModelState(error = true))  // Okhttp, фоновый поток
                _data.value = FeedModelState(error = true) //retrofit, теперь с главного потока
            }

        })
    }

    fun changeContentAndSave(content: String) {
        edited.value?.let { itPost ->
            val text = content.trim()
            if (text != itPost.content) {
                repository.saveAsync(
                    itPost.copy(content = text),
                    object : PostRepository.GetMyCallback<Post> {
                        override fun onSuccess(result: Post) {
                            _postCreated.value = Unit
                            loadPosts()
                        }
                        override fun onError(e: Exception) {
                            _postCreatedError.value = Unit
                            //_data.value = FeedModelState(error = true)
                        }
                    })
            }
        }
        edited.value = defaultPost
    }

    fun likeById(id: Long) {
        val flag = data.value?.posts?.find { it.id == id }?.likedByMe
        if (flag != null) {
            repository.likeByIdAsync(id, flag = flag, object : PostRepository.GetMyCallback<Post> {
                override fun onSuccess(result: Post) {
                    val updatedPosts = _data.value?.posts?.map {
                        if (it.id == id) {
                            result
                        } else {
                            it
                        }
                    }.orEmpty()

                    _data.value = FeedModelState(posts = updatedPosts)
                }

                override fun onError(e: Exception) {
                    _data.value = FeedModelState(error = true)
                }
            })
        }
    }

    fun shareById(id: Long) = repository.shareById(id)
    fun viewById(id: Long) = repository.viewById(id)

    fun removeById(id: Long) {

        val old = _data.value?.posts.orEmpty()

        repository.removeByIdAsync(id, object : PostRepository.GetMyCallback <Unit>{
            override fun onSuccess(result: Unit) {
                _data.value = FeedModelState(posts = _data.value?.posts.orEmpty().filter { it.id != id })
            }
            override fun onError(e: Exception) {
                _data.value = FeedModelState(posts = old)
            }
        })
    }

    fun addLink(id: Long, link: String) = repository.addLink(id, link)


    fun edit(post: Post) {
        edited.value = post
        edited.value?.let {
            repository.saveAsync(it, object : PostRepository.GetMyCallback<Post> {
                override fun onSuccess(result: Post) {
                    _postCreated.value = Unit
                    loadPosts()
                }

                override fun onError(e: Exception) {
                    _postCreatedError.value = Unit
                    //_data.value = FeedModelState(error = true)
                }

            })
        }
        edited.value = defaultPost
    }


}
