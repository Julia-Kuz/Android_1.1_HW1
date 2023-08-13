package ru.netology.nmedia.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityIntentHandlerBinding

class IntentHandlerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityIntentHandlerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            //val text = ""
            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text.isNullOrBlank()) {
                Snackbar.make(
                    binding.root,
                    R.string.error_empty_content,
                    LENGTH_INDEFINITE
                )  // вместо Toast (в пред ДЗ) - всплывающее сообщение
                    .setAction(android.R.string.ok) {
                        finish()
                    }
                    .show()
                return@let
            }
            // TODO: handle text
            binding.contentTextView.setText(text)  //f.ex.
        }
    }
}