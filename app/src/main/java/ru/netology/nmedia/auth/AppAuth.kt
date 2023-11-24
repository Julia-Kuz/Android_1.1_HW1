package ru.netology.nmedia.auth

import android.content.Context
import kotlinx.coroutines.flow.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.AuthApi
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.File
import java.io.IOException

//делаем приватный конструктор - классическая реализация синглтона
//
// когда экземпляр класса создается только в companion object

// (!) Чтобы этот класс инициализировать, будем переопределять application - NMediaApplication

//выполняет роль репозитория => создаю отдельную вьюмодель

class AppAuth private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val idKey = "id"
    private val tokenKey = "token"

    //записываем в MutableStateFlow<AuthState> начальные значения - это чтение из SharedPreferences - можно сразу здесь, в конструкторе
    private val _authStateFlow = MutableStateFlow(
        AuthState (
            prefs.getLong(idKey, 0L),
            prefs.getString(tokenKey, null)
        )
    )

    val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()

    //или через блок init
//    private val _authStateFlow: MutableStateFlow<AuthState>
//    init {
//        val id = prefs.getLong(idKey, 0)
//        val token = prefs.getString(tokenKey, null)
//
//        if (id == 0L || token == null) {
//            _authStateFlow = MutableStateFlow(AuthState())
//            with(prefs.edit()) {
//                clear()
//                apply()
//            }
//        } else {
//            _authStateFlow = MutableStateFlow(AuthState(id, token))
//        }
//    }


    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authStateFlow.value = AuthState(id, token)
        with(prefs.edit()) {
            putLong(idKey, id)
            putString(tokenKey, token)
            apply() //or commit()
        }
    }

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        with(prefs.edit()) {
            clear()
            commit()
        }
    }

    suspend fun checkAuth(login: String, password: String): AuthState =
        try {
            val response =  AuthApi.retrofitService.checkUser(login, password)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val responseBody = response.body()
            responseBody?: throw ApiError(response.code(), response.message())

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

    suspend fun registerUser (login: String, password: String, name: String): AuthState =
        try {
            val response =  AuthApi.retrofitService.registerUser(login, password, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val responseBody = response.body()
            responseBody?: throw ApiError(response.code(), response.message())

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

    suspend fun registerUserWithAvatar (login: String, password: String, name: String, avatar: File): AuthState =
        try {
            val part = MultipartBody.Part.createFormData("file", "image.png", avatar.asRequestBody())
            val loginRequest = login.toRequestBody("text/plain".toMediaType())
            val passwordRequest = password.toRequestBody("text/plain".toMediaType())
            val nameRequest = name.toRequestBody("text/plain".toMediaType())
            val response =  AuthApi.retrofitService.registerWithPhoto (loginRequest, passwordRequest, nameRequest, part)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val responseBody = response.body()
            responseBody?: throw ApiError(response.code(), response.message())

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

    //описываем инициализацию этого синглтона
    companion object {
        @Volatile
        private var instance: AppAuth? = null

        fun getInstance(): AppAuth = synchronized(this) {
            instance ?: throw IllegalStateException(
                "getInstance should be called only after initAuth!" +
                        " AppAuth is not initialized, you must call AppAuth.initializeApp(Context context) first."
            )
        }

        fun initAuth(context: Context): AppAuth = instance ?: synchronized(this) {
            instance ?: buildAuth(context).also { instance = it }  //опять проверяем instance на случай, если два потока одновременно пытаются инициализировать
        }

        private fun buildAuth(context: Context): AppAuth = AppAuth(context) //создаем экземпляр
    }
}

data class AuthState(val id: Long = 0, val token: String? = null) //для хранения id & token