package ru.netology.nmedia.activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentErrorBinding
import ru.netology.nmedia.databinding.FragmentErrorSlideBinding
import ru.netology.nmedia.load

class ErrorSlideFragment : BottomSheetDialogFragment() {

    lateinit var binding: FragmentErrorSlideBinding

    // Переопределяем тему, чтобы использовать нашу с закруглёнными углами
    override fun getTheme() = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentErrorSlideBinding.bind(inflater.inflate(R.layout.fragment_error_slide, container, false))
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        dialog?.let {
            // Находим сам bottomSheet и достаём из него Behaviour
            val bottomSheet = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)

            // Выставляем высоту для состояния и выставляем состояние
            behavior.peekHeight = bottomSheet.height //(COLLAPSED_HEIGHT * density).toInt()
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        binding.errorImage.load("https://mykaleidoscope.ru/x/uploads/posts/2022-09/1663650460_17-mykaleidoscope-ru-p-ochen-silnoe-udivlenie-oboi-22.jpg")

        binding.tryAgainSlide.setOnClickListener {
            dismiss()
        }
    }
}