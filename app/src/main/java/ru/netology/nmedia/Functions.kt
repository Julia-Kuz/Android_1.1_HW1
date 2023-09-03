package ru.netology.nmedia

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