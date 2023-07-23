package ru.netology.nmedia.viewModel

import androidx.lifecycle.ViewModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

class PostViewModel: ViewModel () {
    private val repository : PostRepository  = PostRepositoryInMemoryImpl ()
    val data = repository.get() // св-во data переадресовываем в репозиторий
    fun like () = repository.like() // ф-цию like у viewModel тоже переадресовываем в репозиторий
    fun share () = repository.share()
    fun view () = repository.view()
}