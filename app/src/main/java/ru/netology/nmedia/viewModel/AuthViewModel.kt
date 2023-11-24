package ru.netology.nmedia.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState

class AuthViewModel : ViewModel() {

    // с использованием Live data
//    val dataAuth: LiveData<AuthState> = AppAuth.getInstance()
//        .authStateFlow
//        .asLiveData(Dispatchers.Default)

    // с использованием Flow
    val dataAuth = AppAuth.getInstance().authStateFlow

    val authenticated: Boolean
        get() = AppAuth.getInstance().authStateFlow.value.id != 0L // текущее значение flow, свойство id

    //val authenticated: Boolean = data.value.id != 0L - НЕПРАВИЛЬНО ПИСАТЬ, т.к. значение свойства тогда никогда уже не изменится =>
    //=> проверка пользователя только один раз идет при инициализации класса
    //а метод get() вызывается при каждом обращении к этому свойству => при каждом обращении проверяется, залогинился ли пользователь
}