package ru.netology.nmedia.application

import android.app.Application
import ru.netology.nmedia.auth.AppAuth

class NMediaApplication : Application() {
    //этот onCreate() запускается при самом старте приложения, ни одна строчка приложения до него не выполняется
    override fun onCreate() {
        super.onCreate()
        AppAuth.initAuth(this)  //this - это application, а application - наследник context
    }
}

// (!) Чтобы этот NMediaApplication применился, в манифесте нужно прописать:
// android:name=".application.NMediaApplication"