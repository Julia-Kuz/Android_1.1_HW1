package ru.netology.nmedia.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils.focusAndShowKeyboard
import ru.netology.nmedia.util.AndroidUtils.hideKeyboard
import ru.netology.nmedia.viewModel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun like(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun share(post: Post) {
                viewModel.shareById(post.id)
            }

            override fun view(post: Post) {
                viewModel.viewById(post.id)
            }

            override fun remove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun edit(post: Post) {
                binding.groupEdit.visibility = View.VISIBLE
                viewModel.edit(post)
            }
        }
        )

        binding.recyclerList.adapter = adapter  // получаю доступ к RecyclerView

        binding.editSave.setOnClickListener {

            val text = binding.editContent.text.toString()
            if (text.isEmpty()) {
                Toast.makeText(                     // - показ всплывающего сообщения
                    this,                     // context (1ый параметр)
                    R.string.error_empty_content,   // текст всплывающего сообщения
                    Toast.LENGTH_LONG               // длительность показа сообщения
                ).show()
                return@setOnClickListener
            }
            viewModel.changeContentAndSave(text)

            //viewModel.save()

            binding.editContent.setText("")  // очищает поле ввода после сохранения
            binding.editContent.clearFocus() // сбрасываем фокус, убирается курсор
            hideKeyboard(it)                 // from AndroidUtils
            binding.groupEdit.visibility = View.GONE

        }

        binding.editCancel.setOnClickListener {

            viewModel.cancel()

            binding.editContent.setText("")
            binding.editContent.clearFocus()
            hideKeyboard(it)
            binding.groupEdit.visibility = View.GONE
        }

        viewModel.data.observe(this) { posts ->
            val newPost =
                posts.size > adapter.currentList.size //проверяем, что это добавление поста, а не др действие (лайк и т.п.)
            adapter.submitList(posts) {
                if (newPost) {
                    binding.recyclerList.smoothScrollToPosition(0)
                } // scroll к верхнему сообщению только при добавлении
            }
        }

        viewModel.edited.observe(this) {
            if (it.id == 0L) {
                return@observe
            }
            with(binding.editContent) {
                //requestFocus()
                focusAndShowKeyboard()         // from AndroidUtils
                setText(it.content)
            }
        }
    }
}