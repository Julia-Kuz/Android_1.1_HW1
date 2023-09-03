package ru.netology.nmedia.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.AppActivity
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "server"
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()
        // регистирируем канал, где уведомление будет отображаться:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name =
                getString(R.string.channel_remote_name)                     // название канала
            val descriptionText = getString(R.string.channel_remote_description)  // описание канала
            val importance = NotificationManager.IMPORTANCE_DEFAULT     //важность по умолчанию
            val channel = NotificationChannel(channelId, name, importance).apply {   //создаем канал
                description = descriptionText
            }
            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager  //отправляем канал на регистрацию в систему
            manager.createNotificationChannel(channel)
            // и у приложения появляется свой канал для отправки уведомлений
        }
    }


    override fun onMessageReceived(message: RemoteMessage) {

//        message.data[action]?.let {  //обращаемя к классу message, св-во data по ключу action (в Idea putData("action", "LIKE"))
//            when (Actions.valueOf(it)) {
//                Actions.LIKE -> handleLike(gson.fromJson(message.data[content], InfoLike::class.java))  // ключ content (в Idea putData("content", """{...}"""))
//            }
//        }

// Чтобы не было исключений и приложение не падало, если действия в ENUM классе нет (ДЗ):

        val action = message.data[action]
        if (action != null) {
            if (enumContains(action)) {
                when (Actions.valueOf(action)) {
                    Actions.LIKE -> handleLike(gson.fromJson(message.data[content], PushLike::class.java))
                    Actions.POST -> handlePost(gson.fromJson(message.data[content], PushPost::class.java))
                }
            }
        }
        println(Gson().toJson(message))
    }

    override fun onNewToken(token: String) {
        println(token)
    }


    private fun handleLike(pushLike: PushLike) {
        val intent = Intent(this, AppActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentText(
                getString(
                    R.string.notification_user_liked,
                    pushLike.userName,
                    pushLike.postAuthor,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)  //интент, кот будет отправлен в систему при нажатии (реакция на нажатие на уведомление)
                //сделаем, чтобы запускалась AppActivity (pendingIntent (контейнер для intent) формируем выше)
            .setAutoCancel(true) // чтобы автоматически после нажатия уведомление исчезало
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED          //проверяется есть ли разрешение на уведомление, если есть запускаем notify
        ) {
            NotificationManagerCompat.from(this).notify(
                Random.nextInt(100_000),
                notification
            )
        }

    }

    private fun handlePost(pushPost: PushPost) {
//        val intent = Intent(this, AppActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val text = getString(
            R.string.notification_user_posted,
            pushPost.postAuthor,
            pushPost.postContent
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("New post")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED          //проверяется есть ли разрешение на уведомление, если есть запускаем notify
        ) {
            NotificationManagerCompat.from(this).notify(
                Random.nextInt(100_000),
                notification
            )
        }

    }

}

enum class Actions {
    LIKE,
    POST
}

data class PushLike(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
)
data class PushPost(
    val postId: Long,
    val postAuthor: String,
    val postContent: String
)

private fun enumContains(name: String): Boolean {
    return enumValues<Actions>().any { it.name == name }
}
