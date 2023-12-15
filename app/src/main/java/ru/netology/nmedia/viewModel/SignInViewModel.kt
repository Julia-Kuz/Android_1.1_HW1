package ru.netology.nmedia.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.util.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor (private val appAuth: AppAuth): ViewModel() {
    private val dataAuth = appAuth

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
