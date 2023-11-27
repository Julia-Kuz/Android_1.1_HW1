package ru.netology.nmedia.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.PushToken


//фоновый сервис work manager, чтобы 100% задача была выполнена (в нашем случае - токен отправлен на сервер)

class SendPushTokenWorker (context: Context, workerParameters: WorkerParameters): CoroutineWorker (context, workerParameters) {

    companion object {
        const val TOKEN_KEY = "token_key"
        const val NAME_UNIQUE_WORK = "SendPushTokenWorker"
    }


    override suspend fun doWork(): Result =
        try {
            //получаем токен из вне
            val token = inputData.getString(TOKEN_KEY)

            val pushToken = PushToken(token ?: FirebaseMessaging.getInstance().token.await()) //лекция: либо токен уже готовый из вне, либо обращаемся к Firebase
            PostsApi.retrofitService.sendPushToken(pushToken)
            Result.success() // возвращаем результат-успех
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry() // в сл ошибки возвращаем результат-retry
        }
        //при запуске этой задачи, будем отменять предыдущую, поэтому Result.failure() не возвращаем

}

//все данные, кот передаются в work manager, записываются в БД, чтобы он смог работать в фоне