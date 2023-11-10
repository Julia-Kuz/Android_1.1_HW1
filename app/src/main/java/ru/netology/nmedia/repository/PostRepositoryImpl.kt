package ru.netology.nmedia.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import okhttp3.Call
//import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
//import okhttp3.Response
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError

class PostRepositoryImpl (private val dao: PostDao) : PostRepository {

    override val data = dao.getAll()
        .map(List<PostEntity>::toDto) //map PostEntity into Posts //после изменения на flow - import kotlinx.coroutines.flow.map
       // .flowOn(Dispatchers.Default) //можно опустить, поскольку во viewmodel укажем контекст, на каком потоке работать

    override suspend fun getAll() {
        try {
            val response = PostsApi.retrofitService.getAll() //получаем посты из сети
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message()) //тело ответа

            val newBody = body.map {
                it.copy(saved = true)
            }

            dao.insert(newBody.toEntity()) // записывает ответ в базу данных

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override fun getNewerCount(id: Long): Flow<Int> = flow {//импорт из пакета корутин
        while (true) { // м.сделать бесконечный цикл, т.к.по switchMap (вьюмодель) неактуальные flow будут отменяться
            delay(10_000L)
            val response = PostsApi.retrofitService.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            val newBody = body.map {
                it.copy(saved = true, hidden = true)
            }
            dao.insert(newBody.toEntity())
            emit(body.size)
        }
    }
        .catch {e -> e.printStackTrace() } // e -> throw AppError.from(e) - роняет приложение
        .flowOn(Dispatchers.Default)

    override suspend fun updatePosts() {
        val newPosts = dao.getNewPosts()
        val updatedPosts = newPosts.map {
            it.copy(hidden = false)
        }
        dao.insert(updatedPosts)
    }

    override suspend fun save(post: Post) {

        dao.insert(PostEntity.fromDto(post))

        try {
            val response = PostsApi.retrofitService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val result = response.body() ?: throw ApiError(response.code(), response.message())

            dao.updatePost(PostEntity.fromDto(result))

            //dao.insert(PostEntity.fromDto(result))

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long, flag: Boolean) {
        dao.likeById(id)

            try {
                val response = if (!flag) {
                    PostsApi.retrofitService.likeById(id)
                } else {
                    PostsApi.retrofitService.dislikeById(id)
                }

                if (!response.isSuccessful) {
                    throw ApiError(response.code(), response.message())
                }
                val body = response.body() ?: throw ApiError(response.code(), response.message())
//                dao.likeById(body.id)
            } catch (e: IOException) {
                throw NetworkError
            } catch (e: Exception) {
                throw UnknownError
            }
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
        try {
            val response = PostsApi.retrofitService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            response.body() ?: throw ApiError(response.code(), response.message())
//            dao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override fun shareById(id: Long) {
        TODO("Not yet implemented")
    }

    override fun viewById(id: Long) {
        TODO("Not yet implemented")
    }

    override fun addLink(id: Long, link: String) {
        TODO("Not yet implemented")
    }

    override fun getAllAsync(callback: PostRepository.GetMyCallback<List<Post>>) {
        TODO("Not yet implemented")
    }

    override fun likeByIdAsync(
        id: Long,
        flag: Boolean,
        callback: PostRepository.GetMyCallback<Post>
    ) {
        TODO("Not yet implemented")
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.GetMyCallback<Unit>) {
        TODO("Not yet implemented")
    }

    override fun saveAsync(post: Post, callback: PostRepository.GetMyCallback<Post>) {
        TODO("Not yet implemented")
    }
}