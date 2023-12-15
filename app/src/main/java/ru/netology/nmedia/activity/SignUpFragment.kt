package ru.netology.nmedia.activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignUpBinding
//import ru.netology.nmedia.dependencyInjection.DependencyContainer
import ru.netology.nmedia.viewModel.SignUpViewModel
//import ru.netology.nmedia.viewModel.ViewModelFactory

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private val viewModelSignUp: SignUpViewModel by activityViewModels()

    private val photoResultContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val uri = it.data?.data ?: return@registerForActivityResult  //it.data?.data: data? - это интент, data - данные, т.е.uri, кот нас интересует
            val file = uri.toFile() //uri конвертируем в файл
            viewModelSignUp.setPhoto(uri, file)
        }
    } // этот контракт используем по клику на takephoto и pickPhoto

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignUpBinding.inflate(
            inflater,
            container,
            false
        )

        binding.signUpEnter.isEnabled = false

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val username = binding.userName.text.toString()
                val login = binding.login.text.toString()
                val password = binding.password.text.toString()
                val passwordConfirm = binding.passwordConfirm.text.toString()

                binding.signUpEnter.isEnabled = !(username.isEmpty() || login.isEmpty() || password.isEmpty() || password.isEmpty() || password != passwordConfirm)
            }
        }

        with(binding) {
            userName.addTextChangedListener (textWatcher)
            login.addTextChangedListener (textWatcher)
            password.addTextChangedListener (textWatcher)
            passwordConfirm.addTextChangedListener (textWatcher)
        }


        with(binding) {

            signUpEnter.setOnClickListener {
                if (viewModelSignUp.photo.value == null) {
                    viewModelSignUp.registerAndSetAuth(
                        login.text.toString(),
                        password.text.toString(),
                        userName.text.toString()
                    )
                } else {
                    viewModelSignUp.registerWithAvatarAndSetAuth(
                        login.text.toString(),
                        password.text.toString(),
                        userName.text.toString()
                    )
                }

            }
        }

        viewModelSignUp.response.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_signUpFragment_to_mainFragment)
        }

        viewModelSignUp.error.observe(viewLifecycleOwner) {
            Snackbar.make(binding.signUpEnter, "", Snackbar.LENGTH_LONG)
                .setAnchorView(binding.signUpEnter)
                .setTextMaxLines(3)
                .setText("Check your Username and Password")
                .setBackgroundTint(android.graphics.Color.rgb(0, 102, 255))
                .show()
        }

        // ***   для фото

        binding.takePhoto.setOnClickListener {
            ImagePicker.Builder(this)
                .crop()
                .cameraOnly()
                //.provider(ImageProvider.CAMERA)
                .maxResultSize(1024, 1024)
                //.compress(2048)
                .createIntent { photoResultContract.launch(it) }
            //.createIntent(photoResultContract::launch)
        }

        binding.pickPhoto.setOnClickListener {
            ImagePicker.Builder(this)
                .crop()
                .galleryOnly()
                .compress(1024)
                .createIntent { photoResultContract.launch(it) }
        }

        binding.removePhoto.setOnClickListener {
            viewModelSignUp.clearPhoto()
        }

        viewModelSignUp.photo.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.avatarPreview.setImageResource(R.drawable.ic_account_box_140)
                return@observe
            }

            binding.avatarPreview.setImageURI(it.uri)
        }



        // ***

        return binding.root
    }
}