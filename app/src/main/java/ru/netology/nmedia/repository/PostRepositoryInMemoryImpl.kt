package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var post = Post(
        id = 1,
        author = "Нетология. Университет интернет-профессий будущего",
        content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        published = "21 мая в 18:36",
        likes = 999999,
        likedByMe = false,
        share = 998,
        views = 989
    )
    private val data = MutableLiveData(post)

    override fun get(): LiveData<Post> = data

    override fun like() {
        //post = post.copy(likedByMe = !post.likedByMe, likes = if (post.likedByMe) post.likes++ else post.likes--) - как на лекции, но не работает!  ???
        post.likedByMe = !post.likedByMe
        if (post.likedByMe) post.likes++ else post.likes--
        data.value = post // обновление данных в LiveData
    }

    override fun share() {
        //post = post.copy(share = post.share++) - не работает
        post.share++
        data.value = post
    }

    override fun view() {
        //post = post.copy(views = post.views++) - не работает
        post.views++
        data.value = post
    }

}