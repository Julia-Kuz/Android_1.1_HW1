package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likes = 999999,
            likedByMe = false
        )
        with(binding) {
            author.text = post.author
            content.text = post.content
            published.text = post.published
            likesNumberText.text = numberRepresentation(post.likes)
            if (post.likedByMe) {
                likesIcon.setImageResource(R.drawable.ic_like_red_24)
            }
            shareNumberText.text = numberRepresentation(post.share)
            viewNumberText.text = numberRepresentation(post.views)

            likesIcon.setOnClickListener {
                Log.d("MyLog", "like")
                post.likedByMe = !post.likedByMe
                likesIcon.setImageResource(
                    if (post.likedByMe) R.drawable.ic_like_red_24 else R.drawable.ic_like_24
                )
                if (post.likedByMe) post.likes++ else post.likes--
                likesNumberText.text = numberRepresentation(post.likes)
            }

            shareIcon.setOnClickListener {
                post.share++
                shareNumberText.text = numberRepresentation(post.share)
            }

            viewIcon.setOnClickListener {
                post.views++
                viewNumberText.text = numberRepresentation(post.views)
            }


//            root.setOnClickListener {
//                Log.d("MyLog", "root")
//            }
//            avatar.setOnClickListener {
//                Log.d("MyLog", "avatar")
//            }
//            author.setOnClickListener {
//                Log.d("MyLog", "author")
//            }
//
//            content.setOnClickListener {
//                Log.d("MyLog", "content")
//            }

        }
    }
}