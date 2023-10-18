package com.practicum.playlistmaker.search.domain.entity

import java.io.Serializable

data class Track (
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) : Serializable
