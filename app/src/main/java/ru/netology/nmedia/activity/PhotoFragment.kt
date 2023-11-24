package ru.netology.nmedia.activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentPhotoBinding
import ru.netology.nmedia.load
import ru.netology.nmedia.util.Constants
import ru.netology.nmedia.util.PostDealtWith


class PhotoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPhotoBinding.inflate(
            inflater,
            container,
            false
        )

        val post = PostDealtWith.get()
        val url = "http://10.0.2.2:9999/media/${post.attachment?.url}"

        binding.photoFragment.load(url)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigateUp()
        }

        return binding.root
    }
}
