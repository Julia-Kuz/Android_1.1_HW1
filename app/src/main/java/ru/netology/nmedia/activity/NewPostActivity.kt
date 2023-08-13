package ru.netology.nmedia.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityNewPostBinding
import ru.netology.nmedia.util.AndroidUtils.focusAndShowKeyboard

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addContent.requestFocus()

        binding.ok.setOnClickListener {
            val intent = Intent()
            if (binding.addContent.text.isEmpty()) {
                setResult(RESULT_CANCELED, intent)
            } else {
                intent.putExtra(Intent.EXTRA_TEXT, binding.addContent.text.toString())
                setResult(RESULT_OK, intent)
            }
            finish()
        }
    }
}