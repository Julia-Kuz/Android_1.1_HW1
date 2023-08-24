package ru.netology.nmedia.repository

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post

class PostRepositorySharedPreferencesImpl(
    context: Context,                         //context: Context нужен для доступа к preferences
) : PostRepository {

    private val gson = Gson() //ссылка на GSON
    private val prefs =
        context.getSharedPreferences("repoPref", Context.MODE_PRIVATE) // ссылка на preferences
    private val type = TypeToken.getParameterized(
        List::class.java,
        Post::class.java
    ).type //GSON не всегда понимает generics, поэтому нужно "подсказать", что наш generic список состоит из постов
    private val postsKey = "posts"
    private val nextIdKey = "next_id"

    private var nextId = 1L
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {                                    //этап чтения
        prefs.getString(postsKey, null)?.let {
            posts = gson.fromJson(it, type)  // какой тип указали, такой и результат будет
        }

        nextId = prefs.getLong(nextIdKey, 1)

        data.value = posts
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun save(post: Post) {
        posts = if (post.id == 0L) {
            listOf(
                post.copy(
                    id = nextId++,
                    author = "Me",
                    likedByMe = false,
                    published = "now"
                )
            ) + posts

        } else {
            posts.map {
                if (post.id != it.id) it else it.copy(content = post.content)
            }
        }
        data.value = posts
        sync()
    }

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
            )
        }
        data.value = posts
        sync()
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(share = it.share + 1)
        }
        data.value = posts
        sync()
    }

    override fun viewById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(views = it.views + 1)
        }
        data.value = posts
        sync()
    }

    override fun removeById(id: Long) {
        posts = posts.filterNot { it.id == id }
        data.value = posts
        sync()
    }

    private fun sync() {                       // этап записи, нужно синхронизироваться, если что-то изменилось

        prefs.edit {       //этот вариант автоматически коммитит блок кода (можно не писать commit()/ apply()
            putString(
                postsKey,
                gson.toJson(posts)
            )
            putLong(nextIdKey, nextId)
        }
//        with(prefs.edit()) {
//            putString(key, gson.toJson(posts))
//            apply()
//        }
    }
}
