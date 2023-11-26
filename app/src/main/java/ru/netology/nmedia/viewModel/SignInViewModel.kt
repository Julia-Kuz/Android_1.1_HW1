package ru.netology.nmedia.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.util.SingleLiveEvent

class SignInViewModel : ViewModel() {
    private val dataAuth = AppAuth.getInstance()

    private val _response = SingleLiveEvent<Unit>()
    val response: LiveData<Unit> = _response

    private val _error = SingleLiveEvent<Unit>()
    val error: LiveData<Unit> = _error


    fun checkAndSetAuth(userName: String, password: String) = viewModelScope.launch {
        try {
            val user = dataAuth.checkAuth(userName, password)
            user.token?.let { dataAuth.setAuth(user.id, it) }
            _response.value = Unit
        } catch (e: Exception) {
            _error.value = Unit
        }
    }


    fun removeAuth() = viewModelScope.launch {
        dataAuth.removeAuth()
    }

}
