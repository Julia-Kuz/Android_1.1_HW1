package ru.netology.nmedia.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.numberRepresentation
import ru.netology.nmedia.viewModel.PostViewModel


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        val adapter = PostsAdapter(
            { viewModel.likeById(it.id) },
            { viewModel.shareById(it.id) },
            { viewModel.viewById(it.id) }
        )
        binding.recyclerList.adapter = adapter  // получаю доступ к RecyclerView

        // далее подписываемcя на viewModel (наблюдаем за liveData), "this" - владелец жизн.цикла (зд.- MainActivity),
        // lambda - обработчик, вызывается каждый раз при обновлении данных и при создании класса activity :
        viewModel.data.observe(this) { posts ->
            // adapter.list = posts                  // - просто recyclerView
            adapter.submitList(posts)                // - DiffUtil
        }
    }
}