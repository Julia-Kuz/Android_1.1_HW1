package ru.netology.nmedia.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.Media
import java.io.File


private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/" // в build.gradle в buildTypes прописали в зависимости от сборки

// 1. описываем интерфейс для доступа к нашему серверу, в котором перечисляем все методы

interface AuthApiService {

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun checkUser(@Field("login") login: String, @Field("pass") pass: String): Response<AuthState>

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun registerUser(@Field("login") login: String, @Field("pass") pass: String, @Field("name") name: String): Response<AuthState>

    @Multipart
    @POST("users/registration")
    suspend fun registerWithPhoto(
        @Part("login") login: RequestBody,
        @Part("pass") pass: RequestBody,
        @Part("name") name: RequestBody,
        @Part media: MultipartBody.Part,
    ): Response<AuthState>

}

//Логирование
val loggerAuth = HttpLoggingInterceptor().apply {
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
    .addInterceptor(loggerAuth) //этот клиент передаем в retrofit - ставим после первого, чтобы заголовки "Authorization" уже выводились в logcat
    .build()

// 2. создаем переменную (клиент retrofit), которая знает BASE_URL и умеет парсить и формировать Gson
val retrofitAuth = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(okhttp)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

// 3. в финале создаем объект синглтон, у которого есть свойство retrofitService,
//являющееся результатом создания на клиенте retrofit интерфейса PostsApiService
object AuthApi {
    val retrofitService by lazy {
        retrofitAuth.create(AuthApiService :: class.java)
    }
}

//Теперь, когда у нас есть глобальный синглтон, мы можем его использовать так же,
// как использовали Glide/Picasso, только им требовался контекст, а нашему объекту — нет