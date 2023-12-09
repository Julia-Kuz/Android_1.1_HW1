//package ru.netology.nmedia.dependencyInjection
//
//import android.content.Context
//import androidx.room.Room
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.create
//import ru.netology.nmedia.BuildConfig
//import ru.netology.nmedia.api.AuthApiService
//import ru.netology.nmedia.api.PostsApiService
//import ru.netology.nmedia.auth.AppAuth
//import ru.netology.nmedia.db.AppDb
//import ru.netology.nmedia.repository.PostRepository
//import ru.netology.nmedia.repository.PostRepositoryImpl
//
//class DependencyContainer (private val context: Context) {
//
////  ****************   вся вспомогат инфо в PostsApiService, ее там удаляем и переносим сюда:  ************************
//
//    companion object {
//        private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/" // в build.gradle в buildTypes прописали в зависимости от сборки
//
//        @Volatile
//        private var instance: DependencyContainer? = null
//
//        //чтобы в fun getInstance не передавать контекст (приложения), создаем fun initApp, куда передается контекст
//        fun initApp (context: Context) {
//            instance = DependencyContainer(context)
//        }
//
//        fun getInstance(): DependencyContainer {
//            return instance!!
//            }
//        }
//
//
//    //Логирование
//    val logger = HttpLoggingInterceptor().apply {
//        //устанавливаем уровень логирования
//        if (BuildConfig.DEBUG) {
//            level = HttpLoggingInterceptor.Level.BODY
//        }
//    }
//
//    //**********  создаем appAuth, уже публичное свойство, которое будет внедряться во все необходимые классы и ПЕРЕД private val okhttp ***********
//    val appAuth = AppAuth(context)
//
//    private val okhttp = OkHttpClient.Builder()
//        .addInterceptor { chain ->     //добавляем еще один перехватчик, для добавления token из авторизации в запросы
//            appAuth.authStateFlow.value.token?.let { token -> //обращаемся к св-ву authStateFlow (синглтона AppAuth), где хранятся id & token текущего пользователя, берем token
//                val newRequest = chain.request().newBuilder()       //создаем новый запрос
//                    .addHeader("Authorization", token)                //и в этот запрос добавляем заголовок "Authorization"
//                    .build()
//                return@addInterceptor chain.proceed(newRequest) //этот новый запрос отправить дальше в обработку
//            }
//            chain.proceed(chain.request())          //если token не было, продолжаем обработку с исходным запросом
//        }
//        .addInterceptor(logger) //этот клиент передаем в retrofit - ставим после первого, чтобы заголовки "Authorization" уже выводились в logcat
//        .build()
//
//    // 2. создаем переменную (клиент retrofit), которая знает BASE_URL и умеет парсить и формировать Gson
//    val retrofit = Retrofit.Builder()
//        .baseUrl(BASE_URL)
//        .client(okhttp)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    // 3.создаем объект PostsApiService внутри DependencyContainer
//    val postApiService = retrofit.create<PostsApiService>()
//
//    val authApiService = retrofit.create<AuthApiService>()
//
//
//    // *************   БД  ***************
//    private val appDb = Room.databaseBuilder(context, AppDb::class.java, "app.db") //для доступа к контексту передадим его в конструктор
//        .fallbackToDestructiveMigration()
//        .build()
//
//    private val postDao = appDb.postDao()
//
//
//    //**********  создаем репозиторий, уже публичное свойство, которое будет внедряться во все необходимые классы  ***********
//    val repository: PostRepository = PostRepositoryImpl(
//        postDao,
//        postApiService
//    )
//
//}
//
//// ИТОГ – создали фабрику для создания зависимостей