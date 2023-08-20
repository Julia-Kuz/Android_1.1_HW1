package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post

class PostRepositoryFileImpl(
    private val context: Context,                            //context: Context позволяет работать с файлами, сделали свойством
) : PostRepository {

    private val gson = Gson()
    private val type = TypeToken.getParameterized(
        List::class.java,
        Post::class.java
    ).type //GSON не всегда понимает generics, поэтому нужно "подсказать", что наш generic список состоит из постов

    private val fileNamePost = "posts.json"
    private val fileNameNextId = "next_id.json"

    private var nextId = 1L
    private var posts = emptyList<Post>()
    private val data = MutableLiveData(posts)

    init {
        val postFile = context.filesDir.resolve(fileNamePost)

        posts = if (postFile.exists()) {
            // если файл есть - читаем
            context.openFileInput(fileNamePost).bufferedReader().use {
                gson.fromJson(it, type)
            }
        } else {
            // если нет, записываем пустой массив
           emptyList()
        }

        // то же самое, вариант с вебинара:
//        posts = if (postFile.exists()) {
//            postFile.reader().buffered().use {
//                gson.fromJson(it, type)
//            }
//        } else {
//            emptyList()
//        }

        val nextIdFile = context.filesDir.resolve(fileNameNextId)
        nextId = if (nextIdFile.exists()) {
            nextIdFile.reader().buffered().use {
                gson.fromJson(it, Long ::class.java)     // ссылка на класс, т.к. id у нас Long
            }
        } else 1 // или можно nextId (счетчик не сохраняется в памяти и будет выдавать опять "1"

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

        context.openFileOutput(fileNamePost, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(posts))
        }

        //то же самое, вариант с вебинара

//        context.filesDir.resolve(fileNamePost).writer().buffered().use {
//            it.write(gson.toJson(posts))
//        }

        context.filesDir.resolve(fileNameNextId).writer().buffered().use {
            it.write(gson.toJson(nextId))
        }

    }
}
