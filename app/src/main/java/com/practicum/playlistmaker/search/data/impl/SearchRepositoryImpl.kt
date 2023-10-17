package com.practicum.playlistmaker.search.data.impl

import com.practicum.playlistmaker.utils.Resource
import com.practicum.playlistmaker.search.data.LocalClient
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.search.domain.SearchRepository
import com.practicum.playlistmaker.search.domain.entity.Track

class SearchRepositoryImpl(private val networkClient: NetworkClient, private val localClient: LocalClient):
    SearchRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }
            200 -> {
                Resource.Success((response as TracksSearchResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.getFormattedDuration(),
                        it.getCoverArtwork(),
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                })
            }
            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }

    override fun addTrackToHistory(item: Track) {
        localClient.addTrackToLocalClient(item)
    }

    override fun returnSavedTracks(): ArrayList<Track> {
        return localClient.returnSavedTracks()
    }
    override fun clearSavedTracks() {
        localClient.clearSavedTracks()
    }

}