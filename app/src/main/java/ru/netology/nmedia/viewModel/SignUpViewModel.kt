package ru.netology.nmedia.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor (private val appAuth: AppAuth): ViewModel() {
    private val dataAuth = appAuth

    private val _response = SingleLiveEvent<Unit>()
    val response: LiveData<Unit> = _response

    private val _error = SingleLiveEvent<Unit>()
    val error: LiveData<Unit> = _error


    fun registerAndSetAuth(login: String, password: String, name: String) = viewModelScope.launch {
        try {
            val user = dataAuth.registerUser(login, password, name)
            user.token?.let { dataAuth.setAuth(user.id, it) }
            _response.value = Unit
        } catch (e: Exception) {
            _error.value = Unit
        }
    }


    private val _photo = MutableLiveData<PhotoModel?>(null)
    val photo: LiveData<PhotoModel?>
        get() = _photo

    fun setPhoto(uri: Uri, file: File) {    //это первая часть сохранения, 2 часть - в функции registerWithAvatarAndSetAuth (читаем инфо из значения photo)
        _photo.value = PhotoModel(uri, file)
    }

    fun clearPhoto() {
        _photo.value = null
    }

    fun registerWithAvatarAndSetAuth(login: String, password: String, name: String) =
        viewModelScope.launch {
            try {
                val photoModel = _photo.value //читаем из _photo значение, 2 часть сохранения
                if (photoModel == null) {
                    val user = dataAuth.registerUser(login, password, name)
                    user.token?.let { dataAuth.setAuth(user.id, it) }
                    _response.value = Unit
                } else {
                    val user = dataAuth.registerUserWithAvatar(login, password, name, photoModel.file)
                    user.token?.let { dataAuth.setAuth(user.id, it) }
                    _response.value = Unit
                }

            } catch (e: Exception) {
                _error.value = Unit
            }
        }

}
