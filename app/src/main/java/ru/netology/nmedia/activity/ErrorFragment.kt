package ru.netology.nmedia.activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentErrorBinding
import ru.netology.nmedia.load


class ErrorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentErrorBinding.inflate(
            inflater,
            container,
            false
        )

        binding.imageError.load("https://mykaleidoscope.ru/x/uploads/posts/2022-09/1663650460_17-mykaleidoscope-ru-p-ochen-silnoe-udivlenie-oboi-22.jpg")

        binding.tryAgain.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }
}