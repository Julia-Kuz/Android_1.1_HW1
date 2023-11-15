package ru.netology.nmedia.activity

import android.R.layout
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentEditPostBinding
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.load
import ru.netology.nmedia.util.AttachmentType
import ru.netology.nmedia.util.PostDealtWith
import ru.netology.nmedia.viewModel.PostViewModel


class EditPostFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEditPostBinding.inflate(
            inflater,
            container,
            false
        )

        val post = PostDealtWith.get()

        binding.editContent.setText(post.content)
        binding.editContent.requestFocus()
        binding.editContent.movementMethod = ScrollingMovementMethod()


        binding.saveButton.setOnClickListener {
            val postEdited = post.copy(content = binding.editContent.text.toString())
            if (postEdited.attachment?.url != null) {
                viewModel.edit(postEdited.copy())
            } else {
                viewModel.edit(postEdited.copy(attachment = null))
            }

            //findNavController().navigateUp()
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.postCreatedError.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_editPostFragment_to_errorFragment)
        }

        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }
}