package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toFile
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
//import ru.netology.nmedia.dependencyInjection.DependencyContainer
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.Constants
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewModel.PostViewModel
//import ru.netology.nmedia.viewModel.ViewModelFactory

@AndroidEntryPoint
class NewPostFragment : Fragment() {
    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by activityViewModels()

    private val photoResultContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val uri = it.data?.data ?: return@registerForActivityResult  //it.data?.data: data? - это интент, data - данные, т.е.uri, кот нас интересует
            val file = uri.toFile() //uri конвертируем в файл
            viewModel.setPhoto(uri, file)
        }
    } // этот контракт используем по клику на takephoto и pickPhoto

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

        binding.edit.requestFocus()

        arguments?.textArg
            ?.let(binding.edit::setText)
        viewModel.clearPhoto()

// Пункт меню c моей темой приложения: (!) <style name="Base.Theme.First_App" parent="Theme.Material3.DayNight.NoActionBar">

        val toolbar: Toolbar = binding.toolbar
        toolbar.inflateMenu(R.menu.menu_new_post)

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.saveNewPost -> {
                    viewModel.changeContentAndSave(binding.edit.text.toString())
                    AndroidUtils.hideKeyboard(requireView())
                    true
                }
                else -> false
            }
        }


// Добавляем пункт меню c темой приложения (лекция) <!--style name="Base.Theme.First_App" parent="Theme.MaterialComponents.DayNight.DarkActionBar"-->:
//
//        requireActivity().addMenuProvider(object : MenuProvider {
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menuInflater.inflate(R.menu.menu_new_post, menu)
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
//                when (menuItem.itemId) {
//                    R.id.saveNewPost -> {
//                        viewModel.changeContentAndSave(binding.edit.text.toString())
//                        AndroidUtils.hideKeyboard(requireView())
//                        true
//                    }
//                    else -> false
//                }
//
//        })


        binding.takePhoto.setOnClickListener {
            ImagePicker.Builder(this)
                .crop()
                .cameraOnly()
                //.provider(ImageProvider.CAMERA)
                .maxResultSize(2048, 2048)
                //.compress(2048)
                .createIntent { photoResultContract.launch(it) }
                //.createIntent(photoResultContract::launch)
        }

        binding.pickPhoto.setOnClickListener {
            ImagePicker.Builder(this)
                .crop()
                .galleryOnly()
                .compress(2048)
                .createIntent { photoResultContract.launch(it) }
            //.createIntent(photoResultContract::launch)
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.photoContainer.isGone = true
                return@observe
            }

            binding.photoContainer.isVisible = true
            binding.photoPreview.setImageURI(it.uri)
        }

        binding.removePhoto.setOnClickListener {
            viewModel.clearPhoto()
        }


        requireActivity().onBackPressedDispatcher.addCallback(this) {
            // viewModel.draft = binding.addContent.text.toString()       // черновик с помощью VieModel
            (activity as AppActivity).getSharedPreferences(
                Constants.DRAFT_PREF_NAME,
                Context.MODE_PRIVATE
            ).edit().apply {
                putString(Constants.DRAFT_KEY, binding.edit.text.toString())
                apply()
            }
            findNavController().navigateUp()
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.postCreatedError.observe(viewLifecycleOwner) {

            //вариант Snackbar
//            Snackbar.make(binding.ok, "", Snackbar.LENGTH_LONG)
//                .setAnchorView(binding.edit)
//                .setTextMaxLines(3)
//                .setText("Sorry :( \nSomething went wrong \nTry again")
//                .setBackgroundTint(android.graphics.Color.rgb(0, 102, 255))
//                .show()

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