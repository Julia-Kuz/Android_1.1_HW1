package ru.netology.nmedia.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

class PostRepositoryImpl : PostRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    private val postsType = object : TypeToken<List<Post>>() {}.type //нужно для парсинга

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999/api/slow"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAllAsync(callback: PostRepository.GetMyCallback <List<Post>>) {

        PostsApi.retrofitService.getAll()  // создали запрос
            //при импорте интерфейса Callback нужно теперь выбирать библиотеку Retrofit
            .enqueue(object : Callback <List<Post>> {

                override fun onFailure(call: retrofit2.Call<List<Post>>, t: Throwable) {
                    callback.onError(Exception(t))
                }

                override fun onResponse(
                    call: retrofit2.Call<List<Post>>,
                    response: Response<List<Post>>
                ) {
                    if (response.isSuccessful) {
                        callback.onSuccess(response.body() ?: throw RuntimeException ("empty"))
                    } else {
                        Log.d("Mylog", "error code: ${response.code()} with ${response.message()}")
                        callback.onError(RuntimeException ("error code: ${response.code()} with ${response.message()}"))
                    }
                }
            })
    }

    override fun likeByIdAsync(
        id: Long,
        flag: Boolean,
        callback: PostRepository.GetMyCallback<Post>
    ) {
        val request: retrofit2.Call<Post> = if (!flag) {
            PostsApi.retrofitService.likeById(id)
        } else {
            PostsApi.retrofitService.dislikeById(id)
        }

        request.enqueue(object : Callback <Post> {

            override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                callback.onError(Exception(t))
            }

            override fun onResponse(call: retrofit2.Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    callback.onSuccess(response.body() ?: throw RuntimeException ("error"))
                } else {
                    Log.d("Mylog", "error code: ${response.code()} with ${response.message()}")
                    callback.onError(RuntimeException ("error code: ${response.code()} with ${response.message()}"))
                }
            }
        })
    }

    override fun saveAsync(post: Post, callback: PostRepository.GetMyCallback <Post>) {
        PostsApi.retrofitService.save(post)
            .enqueue(object : Callback <Post> {

                override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                    callback.onError(Exception(t))
                }

                override fun onResponse(call: retrofit2.Call<Post>, response: Response<Post>) {

                    if (response.isSuccessful) {
                        callback.onSuccess(response.body() ?: throw RuntimeException ("empty body"))
                    } else {
                        Log.d("Mylog", "error code: ${response.code()} with ${response.message()}")
                        callback.onError(RuntimeException ("error code: ${response.code()} with ${response.message()}"))
                    }
                }
            })
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.GetMyCallback<Unit>) {
        PostsApi.retrofitService.removeById(id)
            .enqueue(object : Callback <Unit> {

                override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                    callback.onError(Exception(t))
                }

                override fun onResponse(call: retrofit2.Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        callback.onSuccess(Unit)
                    } else {
                        Log.d("Mylog", "error code: ${response.code()} with ${response.message()}")
                        callback.onError(RuntimeException ("error code: ${response.code()} with ${response.message()}"))
                    }
                }
            })
    }


    //**************

    override fun getAll(): List<Post> {
        val request = Request.Builder()
            .url("${BASE_URL}/posts")
            .build()      // создали запрос

        val call = client.newCall(request) // создали сетевой вызов
        val response = call.execute() //вызов запустили, получили ответ
        val responseString = response.body?.string() ?: error("Body is null") //ответ в виде строки
        return gson.fromJson(
            responseString,
            postsType
        ) // ответ парсим к нужному типу с помощью gson
    }

    override fun likeById(id: Long, flag: Boolean) : Post {
        if (!flag) {
            val request = Request.Builder()
                .url("${BASE_URL}/posts/${id}/likes")
                .post(gson.toJson("${BASE_URL}/posts/${id}/likes").toRequestBody(jsonType))
                .build()

            return client.newCall(request)
                .execute()
                .let { it.body?.string() }
                .let {
                    gson.fromJson(it, Post::class.java)
                }
        } else {
            val request = Request.Builder()
                .url("${BASE_URL}/posts/${id}/likes")
                .delete(gson.toJson("${BASE_URL}/posts/${id}/likes").toRequestBody(jsonType))
                .build()

            return client.newCall(request)
                .execute()
                .let { it.body?.string() }
                .let {
                    gson.fromJson(it, Post::class.java)
                }
        }
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/posts/$id")
            .delete()
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun save(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
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
}