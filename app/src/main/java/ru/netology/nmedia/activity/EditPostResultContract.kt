package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.nmedia.dto.Post

class EditPostResultContract : ActivityResultContract<Post, Post?>() {
    private lateinit var inputPost: Post
    override fun createIntent(context: Context, input: Post): Intent {
        inputPost = input.copy()
        return Intent(context, EditPostFragment::class.java).putExtra(
            Intent.EXTRA_TEXT,
            inputPost.content
        )
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Post? =
        if (resultCode == Activity.RESULT_OK) {
            inputPost.copy(content = intent?.getStringExtra(Intent.EXTRA_TEXT).toString())
        } else null
}