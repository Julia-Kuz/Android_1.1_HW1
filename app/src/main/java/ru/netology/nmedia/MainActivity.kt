package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewModel.PostViewModel


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel by viewModels<PostViewModel>() // ф-ция generic => прописываем, что должна возвращать экземпляр класса PostViewModel

        // далее подписываемcя на viewModel (наблюдаем за liveData), "this" - владелец жизн.цикла (зд.- MainActivity),
        // lambda - обработчик, вызывается каждый раз при обновлении данных и при создании класса activity :
        viewModel.data.observe(this) { post ->
            with(binding) {
                author.text = post.author
                content.text = post.content
                published.text = post.published
                likesNumberText.text = numberRepresentation(post.likes)
                //Log.d("MyLog", "presentation like ${post.likes}}")
                likesIcon.setImageResource(
                    if (post.likedByMe) R.drawable.ic_like_red_24 else R.drawable.ic_like_24
                )
                shareNumberText.text = numberRepresentation(post.share)
                //Log.d("MyLog", "presentation share ${post.share}")
                viewNumberText.text = numberRepresentation(post.views)
                //Log.d("MyLog", "presentation views ${post.views}")
            }
        }

        binding.likesIcon.setOnClickListener {
            //Log.d("MyLog", "like")
            viewModel.like()
        }

        binding.shareIcon.setOnClickListener {
            //Log.d("MyLog", "share")
            viewModel.share()
        }

        binding.viewIcon.setOnClickListener {
            //Log.d("MyLog", "view")
            viewModel.view()
        }

    }
}