package ru.netology.nmedia.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.viewModel.AuthViewModel
import ru.netology.nmedia.viewModel.SignInViewModel

class SignInFragment : Fragment() {

    private val viewModelSignIn: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignInBinding.inflate(
            inflater,
            container,
            false
        )

        binding.signInEnter.isEnabled = false

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val login = binding.userLogin.text.toString()
                val password = binding.password.text.toString()

                binding.signInEnter.isEnabled = !(login.isEmpty() || password.isEmpty())
            }
        }

        with(binding) {
            userLogin.addTextChangedListener (textWatcher)
            password.addTextChangedListener (textWatcher)
        }


        with(binding) {

            signInEnter.setOnClickListener {
                viewModelSignIn.checkAndSetAuth(
                    userLogin.text.toString(),
                    password.text.toString()
                )
            }
        }

        viewModelSignIn.response.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_signInFragment_to_mainFragment)
        }

        viewModelSignIn.error.observe(viewLifecycleOwner) {
            Snackbar.make(binding.signInEnter, "", Snackbar.LENGTH_LONG)
                .setAnchorView(binding.userLogin)
                .setTextMaxLines(3)
                .setText("Check your Username and Password")
                .setBackgroundTint(android.graphics.Color.rgb(0, 102, 255))
                .show()
        }

        return binding.root
    }
}