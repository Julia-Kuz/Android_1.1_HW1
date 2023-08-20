package ru.netology.nmedia.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewModel.PostViewModel

class NewPostFragment : Fragment() {
    companion object {
        //        private const val TEXT_KEY = "TEXT_KEY"
        var Bundle.textArg: String? by StringArg
//            set(value) = putString(TEXT_KEY, value)   //вынесли в StringArg (в папке util)
//            get() = getString(TEXT_KEY)
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        binding.addContent.requestFocus()

        arguments?.textArg
            ?.let(binding.addContent::setText)

        binding.ok.setOnClickListener {
            viewModel.changeContentAndSave(binding.addContent.text.toString())
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()

        }
        return binding.root
    }
}