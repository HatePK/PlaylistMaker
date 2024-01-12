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

    fun editSecCase (word: String, number: Int): String {
        if (number < 10) {
            return if (number % 10 == 0) {
                word
            } else if (number % 10 == 1) {
                word + "а"
            } else if (number % 10 in 2..4) {
                word + "ы"
            } else {
                word
            }
        } else if (number in 10 .. 19) {
            return word
        } else {
            return if (number % 10 == 0) {
                word
            } else if (number % 10 == 1) {
                word + "а"
            } else if (number % 10 in 2..4) {
                word + "ы"
            } else {
                word
            }
        }
    }
}