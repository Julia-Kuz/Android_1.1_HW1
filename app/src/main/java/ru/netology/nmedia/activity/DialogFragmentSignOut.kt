package ru.netology.nmedia.activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentDialogSignOutBinding
import ru.netology.nmedia.viewModel.SignInViewModel


class DialogFragmentSignOut : Fragment() {

    private val viewModelSignIn: SignInViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDialogSignOutBinding.inflate(
            inflater,
            container,
            false
        )

        binding.noStay.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.yesSignOut.setOnClickListener {
            viewModelSignIn.removeAuth()
            findNavController().navigate(R.id.action_dialogFragmentSignOut_to_mainFragment)
        }
        return binding.root
    }
}