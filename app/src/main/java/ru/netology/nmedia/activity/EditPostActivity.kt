package ru.netology.nmedia.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityEditPostBinding

class EditPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            editContent.setText(intent.getStringExtra(Intent.EXTRA_TEXT))
            editContent.requestFocus()
        }

        binding.saveButton.setOnClickListener {
            val intent = Intent()
            if (binding.editContent.text.toString().isEmpty()) {
                Snackbar.make(
                    binding.root,
                    R.string.error_empty_content,
                    BaseTransientBottomBar.LENGTH_INDEFINITE
                )
                    .setAction(android.R.string.ok) {
                        finish()
                    }
                    .show()
                return@setOnClickListener
            } else {
                intent.putExtra(Intent.EXTRA_TEXT, binding.editContent.text.toString())
                setResult(RESULT_OK, intent)
                finish()
            }

        }

        binding.cancelButton.setOnClickListener {
            finish()
        }
    }
}