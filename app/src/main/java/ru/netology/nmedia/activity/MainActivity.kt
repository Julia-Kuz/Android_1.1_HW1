package ru.netology.nmedia.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.launch
import androidx.activity.viewModels
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
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

        // прсото preferences, не shared
//        run {
//            val preferences = getPreferences(Context.MODE_PRIVATE)
//            preferences.edit().apply {
//                putString("keyWord", "value") // keyWord - сама придумываю
//                apply() // commit() - синхронно, apply()- асинхронно
//            }
//        }
//
//        run {
//            getPreferences(Context.MODE_PRIVATE)
//                .getString("keyWord", null)?.let {
//                    Snackbar.make(binding.root, it, BaseTransientBottomBar.LENGTH_INDEFINITE)
//                        .show()
//                }
//        }

        val viewModel: PostViewModel by viewModels()

        val editPostLauncher =
            registerForActivityResult(EditPostResultContract()) { result ->
                result ?: return@registerForActivityResult
                viewModel.edit(result)
            }

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun like(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun share(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(
                        intent,
                        getString(R.string.chooser_share_post)
                    ) //создается chooser - выбор между приложениями
                startActivity(shareIntent)

                //viewModel.shareById(post.id)
            }

            override fun view(post: Post) {
                viewModel.viewById(post.id)
            }

            override fun remove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun edit(post: Post) {
                editPostLauncher.launch(post)
            }

            override fun play(post: Post) {
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoLink)) // в ДЗ

                val intent =
                    Intent(Intent.ACTION_VIEW).apply {             //на сайте https://developer.android.com/guide/components/intents-common#Music
                        data = Uri.parse(post.videoLink)
                    }
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
        }
        )

        binding.recyclerList.adapter = adapter  // получаю доступ к RecyclerView


        viewModel.data.observe(this) { posts ->
            val newPost =
                posts.size > adapter.currentList.size //проверяем, что это добавление поста, а не др действие (лайк и т.п.)
            adapter.submitList(posts) {
                if (newPost) {
                    binding.recyclerList.smoothScrollToPosition(0)
                } // scroll к верхнему сообщению только при добавлении
            }
        }

        val newPostLauncher = registerForActivityResult(NewPostResultContract()) { result ->
            result ?: return@registerForActivityResult
            viewModel.changeContentAndSave(result)
        }

        binding.addPost.setOnClickListener {
            newPostLauncher.launch()
        }

    }
}