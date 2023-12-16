package ru.netology.nmedia.auth

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.FirebaseMessagingKtxRegistrar
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.AuthApiService
import ru.netology.nmedia.api.PostsApiService
//import ru.netology.nmedia.dependencyInjection.DependencyContainer
//import ru.netology.nmedia.api.AuthApi
//import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.workers.SendPushTokenWorker
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

//делаем приватный конструктор - классическая реализация синглтона
//
// когда экземпляр класса создается только в companion object

// (!) Чтобы этот класс инициализировать, будем переопределять application - NMediaApplication

//выполняет роль репозитория => создаю отдельную вьюмодель

@Singleton
class AppAuth @Inject constructor  (
    @ApplicationContext
    private val context: Context
    ) {
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
//        sendPushToken() // при наличии фонового worker эту строчку можно убрать из старта приложения
//    }


    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authStateFlow.value = AuthState(id, token)
        with(prefs.edit()) {
            putLong(idKey, id)
            putString(tokenKey, token)
            apply() //or commit()
        }
        sendPushToken()
    }

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        with(prefs.edit()) {
            clear()
            commit()
        }
        sendPushToken()
    }

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun getAuthApiService(): AuthApiService
    }

    suspend fun checkAuth(login: String, password: String): AuthState =
        try {
            val entryPoint = EntryPointAccessors.fromApplication(context, AppAuthEntryPoint::class.java)
            val response =  entryPoint.getAuthApiService().checkUser(login, password)
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
            //val response =  DependencyContainer.getInstance().authApiService.registerUser(login, password, name)
            val entryPoint = EntryPointAccessors.fromApplication(context, AppAuthEntryPoint::class.java)
            val response =  entryPoint.getAuthApiService().registerUser(login, password, name)
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
            //val response =  DependencyContainer.getInstance().authApiService.registerWithPhoto (loginRequest, passwordRequest, nameRequest, part)
            val entryPoint = EntryPointAccessors.fromApplication(context, AppAuthEntryPoint::class.java)
            val response =  entryPoint.getAuthApiService().registerWithPhoto (loginRequest, passwordRequest, nameRequest, part)
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

    fun sendPushToken(token: String? = null) {

        val request = OneTimeWorkRequestBuilder<SendPushTokenWorker>()
            .setConstraints(    // это то, при каких условиях worker должен быть запущен
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInputData(    //  задаем  входной параметр(ы)
                Data.Builder()
                    .putString(SendPushTokenWorker.TOKEN_KEY, token)
                    .build()
            )
            .build()

        WorkManager.getInstance(context)   //чтобы получить context в конструкторе сделали его свойством: class AppAuth private constructor(private val context: Context)
                    .enqueueUniqueWork(SendPushTokenWorker.NAME_UNIQUE_WORK, ExistingWorkPolicy.REPLACE, request)   // далее планируем задачу


        //  без фонового сервиса work manager
//        CoroutineScope(Dispatchers.Default).launch {
//            try {
//                //val pushToken = PushToken(token ?: Firebase.messaging.token.await())
//                val pushToken = PushToken(token ?: FirebaseMessaging.getInstance().token.await()) //лекция: либо токен уже готовый из вне, либо обращаемся к Firebase
//                PostsApi.retrofitService.sendPushToken(pushToken) // !  PostsApi.retrofitService при внедрении зависимостей изменить надо
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
    }

}

data class AuthState(val id: Long = 0, val token: String? = null) //для хранения id & token