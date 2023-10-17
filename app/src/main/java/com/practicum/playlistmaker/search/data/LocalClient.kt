package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.domain.entity.Track

interface LocalClient {
    fun addTrackToLocalClient(item: Track)
    fun returnSavedTracks() : ArrayList<Track>
    fun clearSavedTracks()
}