package ru.netology.nmedia.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PushToken

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

    @Multipart
    @POST("media")
    suspend fun saveMedia(@Part media: MultipartBody.Part): Response<Media>

    @POST("users/push-tokens")
    suspend fun sendPushToken (@Body pushToken: PushToken): Response<Unit>

//    @FormUrlEncoded
//    @POST("users/authentication")
//    suspend fun checkUser(@Field("login") login: String, @Field("pass") pass: String): Response<AuthState>

}

//Логирование
val logger = HttpLoggingInterceptor().apply {
    //устанавливаем уровень логирования
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

private val okhttp = OkHttpClient.Builder()
    .addInterceptor { chain ->     //добавляем еще один перехватчик, для добавления token из авторизации в запросы
        AppAuth.getInstance().authStateFlow.value.token?.let { token -> //обращаемся к св-ву authStateFlow (синглтона AppAuth), где хранятся id & token текущего пользователя, берем token
            val newRequest = chain.request().newBuilder()       //создаем новый запрос
                .addHeader("Authorization", token)                //и в этот запрос добавляем заголовок "Authorization"
                .build()
            return@addInterceptor chain.proceed(newRequest) //этот новый запрос отправить дальше в обработку
        }
        chain.proceed(chain.request())          //если token не было, продолжаем обработку с исходным запросом
    }
    .addInterceptor(logger) //этот клиент передаем в retrofit - ставим после первого, чтобы заголовки "Authorization" уже выводились в logcat
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
