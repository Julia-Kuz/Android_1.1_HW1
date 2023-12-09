package ru.netology.nmedia.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ru.netology.nmedia.auth.AppAuth
//import ru.netology.nmedia.dependencyInjection.DependencyContainer

@HiltAndroidApp
class NMediaApplication : Application()

//class NMediaApplication : Application() {
//    //этот onCreate() запускается при самом старте приложения, ни одна строчка приложения до него не выполняется
//    override fun onCreate() {
//        super.onCreate()
//        DependencyContainer.initApp(this)  //this - это application, а application - наследник context
//    }
//
//}


// (!) Чтобы этот NMediaApplication применился, в манифесте нужно прописать:
// android:name=".application.NMediaApplication"