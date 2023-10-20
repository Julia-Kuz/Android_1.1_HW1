package ru.netology.nmedia.activity

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.load.resource.bitmap.CenterInside
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.Constants
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewModel.PostViewModel

class NewPostFragment : Fragment() {
    companion object {
        var Bundle.textArg: String? by StringArg
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
           // viewModel.draft = ""                        //  черновик с помощью VieModel
            (activity as AppActivity).getSharedPreferences(Constants.DRAFT_PREF_NAME, Context.MODE_PRIVATE).edit().apply {
                putString(Constants.DRAFT_KEY, "")
                apply()
            }
            AndroidUtils.hideKeyboard(requireView())
           // findNavController().navigateUp()  //переносим в подписку на postCreated (Life Cycle Event)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
           // viewModel.draft = binding.addContent.text.toString()       // черновик с помощью VieModel
            (activity as AppActivity).getSharedPreferences(Constants.DRAFT_PREF_NAME, Context.MODE_PRIVATE).edit().apply {
                putString(Constants.DRAFT_KEY, binding.addContent.text.toString())
                apply()
            }
           // findNavController().navigateUp()
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.postCreatedError.observe(viewLifecycleOwner) {

            //вариант BottomSheetDialogFragment
            ErrorSlideFragment().show(parentFragmentManager, "error")

            //вариант Toast
//            val toast = Toast.makeText(context, "Sorry :( \nSomething went wrong", Toast.LENGTH_LONG)
//            toast.setGravity(Gravity.CENTER, 0, 0 )
//            toast.show()

            //ErrorFragment
//            findNavController().navigate(R.id.action_newPostFragment_to_errorFragment)
        }

        return binding.root
    }
}