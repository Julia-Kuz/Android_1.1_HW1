package ru.netology.nmedia.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.AuthApiService
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.auth.AppAuth
//import ru.netology.nmedia.dependencyInjection.DependencyContainer
//import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.PushToken
import javax.inject.Inject


//фоновый сервис work manager, чтобы 100% задача была выполнена (в нашем случае - токен отправлен на сервер)

class SendPushTokenWorker (@ApplicationContext private val context: Context, workerParameters: WorkerParameters): CoroutineWorker (context, workerParameters) {

    companion object {
        const val TOKEN_KEY = "token_key"
        const val NAME_UNIQUE_WORK = "SendPushTokenWorker"
    }

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppPostEntryPoint {
        fun getPostApiService(): PostsApiService
    }

    override suspend fun doWork(): Result =
        try {
            //получаем токен из вне
            val token = inputData.getString(TOKEN_KEY)

            val pushToken = PushToken(token ?: FirebaseMessaging.getInstance().token.await()) //лекция: либо токен уже готовый из вне, либо обращаемся к Firebase
            //DependencyContainer.getInstance().postApiService.sendPushToken(pushToken)
            val entryPoint = EntryPointAccessors.fromApplication(context, AppPostEntryPoint::class.java)
            entryPoint.getPostApiService().sendPushToken(pushToken)
            Result.success() // возвращаем результат-успех
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry() // в сл ошибки возвращаем результат-retry
        }
        //при запуске этой задачи, будем отменять предыдущую, поэтому Result.failure() не возвращаем

}

//все данные, кот передаются в work manager, записываются в БД, чтобы он смог работать в фоне