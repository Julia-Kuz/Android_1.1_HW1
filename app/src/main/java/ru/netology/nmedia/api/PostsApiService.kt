package ru.netology.nmedia.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.dto.Post

private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/" // в build.gradle в buildTypes прописали в зависимости от сборки

// 1. описываем интерфейс для доступа к нашему серверу, в котором перечисляем все методы
// ! модели данных лучше отделять на разных слоях =>
// в качестве модели данных для retrofit лучше не брать модель <Post>, нужно сделать как для Room - свой PostDao & PostEntity

interface PostsApiService {
    @GET ("posts")
    suspend fun getAll () : Response <List <Post>>   // импортировать нужно из retrofit2

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response <Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response <Post>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response <Unit>

}

//Логирование
val logger = HttpLoggingInterceptor().apply {
    //устанавливаем уровень логирования
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

private val okhttp = OkHttpClient.Builder() //этот клиент передаем в retrofit
    .addInterceptor(logger)
    .build()

// 2. создаем переменную (клиент retrofit), которая знает BASE_URL и умеет парсить и формировать Gson
val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(okhttp)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

// 3. в финале создаем объект синглтон, у которого есть свойство retrofitService,
//являющееся результатом создания на клиенте retrofit интерфейса PostsApiService
object PostsApi {
    val retrofitService by lazy {
        retrofit.create(PostsApiService :: class.java)
    }
}

//Теперь, когда у нас есть глобальный синглтон, мы можем его использовать так же,
// как использовали Glide/Picasso, только им требовался контекст, а нашему объекту — нет

