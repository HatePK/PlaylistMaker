package com.practicum.playlistmaker.search.data.impl

import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.utils.Resource
import com.practicum.playlistmaker.search.data.LocalClient
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.data.dto.TracksSearchResponse
import com.practicum.playlistmaker.search.domain.SearchRepository
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val localClient: LocalClient,
    private val database: AppDatabase
):
    SearchRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }
            200 -> {

                val favouritesId = database.trackDao().getFavouritesId()

                emit(Resource.Success((response as TracksSearchResponse).results.map {
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
                        it.previewUrl,
                        isFavorite = favouritesId.contains(it.trackId)
                    )
                }))
            }
            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }

    override fun addTrackToHistory(item: Track) {
        localClient.addTrackToLocalClient(item)
    }

    override suspend fun returnSavedTracks(): ArrayList<Track> {
        val favouritesId = database.trackDao().getFavouritesId()
        val localTracks = localClient.returnSavedTracks()

        localTracks.map {it.isFavorite = favouritesId.contains(it.trackId) }
        return localTracks
    }
    override fun clearSavedTracks() {
        localClient.clearSavedTracks()
    }

}