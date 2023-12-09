//package ru.netology.nmedia.viewModel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import ru.netology.nmedia.auth.AppAuth
//import ru.netology.nmedia.repository.PostRepository
//
//class ViewModelFactory (
//    private val repository: PostRepository,
//    private val appAuth: AppAuth
//    ): ViewModelProvider.Factory {
//
//    //чтобы подавить предупреждение, используется аннотация, где указывается тип предупреждения:
//
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T =
//       when {
//           modelClass.isAssignableFrom(PostViewModel::class.java) -> PostViewModel(repository, appAuth) as T
//           modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(appAuth) as T
//           modelClass.isAssignableFrom(SignUpViewModel::class.java) -> SignUpViewModel(appAuth) as T
//           modelClass.isAssignableFrom(SignInViewModel::class.java) -> SignInViewModel(appAuth) as T
//           else -> error("Unknown class: $modelClass")
//       }
//
//}