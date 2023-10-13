package ru.netology.nmedia

import android.widget.ImageView
import androidx.core.view.marginEnd
import androidx.core.view.marginTop
import com.bumptech.glide.Glide

fun numberRepresentation(number: Int): String {

    return when (number) {
        in 0 until 1000 -> number.toString()
        in 1000 until 1100 -> (number / 1000).toString() + "K"
        in 1100 until 10_000 -> {
            if (number % 1000 == 0) (number / 1000).toString() + "K" else
                ((number.toDouble()) / 1000).toString().take(3) + "K"
        }

        in 10_000 until 1000_000 -> (number / 1000).toString() + "K"
        in 1000_000 until 1100_000 -> (number / 1000_000).toString() + "M"
        else -> {
            if (number % 1000_000 == 0) (number / 1000_000).toString() + "M" else
                ((number.toDouble()) / 1000_000).toString().take(3) + "M"
        }
    }
}

fun ImageView.loadCircle (url: String) {
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.ic_downloading_24)
        .error(R.drawable.ic_error_24)
        .timeout(10_000)
        .circleCrop()
        .into(this)
}

fun ImageView.load (url: String) {
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.ic_downloading_24)
        .error(R.drawable.ic_error_24)
        .timeout(10_000)
        .into(this)
}


