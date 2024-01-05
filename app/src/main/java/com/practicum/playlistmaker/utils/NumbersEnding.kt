package com.practicum.playlistmaker.utils

class NumbersEnding  {
    fun edit (word: String, number: Int): String {
        return if (number % 10 == 0) {
            word + "ов"
        } else if (number % 10 == 1) {
            word
        } else if (number % 10 in 2..4) {
            word + "а"
        } else {
            word + "ов"
        }
    }
}