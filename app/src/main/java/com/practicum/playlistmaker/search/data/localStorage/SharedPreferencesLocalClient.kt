package com.practicum.playlistmaker.search.data.localStorage

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.data.LocalClient
import com.practicum.playlistmaker.search.domain.entity.Track


class SharedPreferencesLocalClient (private val sharedPreferences: SharedPreferences): LocalClient {
    companion object {
        const val SAVED_TRACKS_MAX = 10
        const val SAVED_TRACKS = "saved_tracks"
    }

    private val sharedPrefsTracks = sharedPreferences.getString(SAVED_TRACKS,  null)

    override fun addTrackToLocalClient(item: Track) {
        if (sharedPrefsTracks != null) {
            val tracksArray = createTrackListFromJson(sharedPrefsTracks)
            val doesTrackExist = doesTrackExist(item, tracksArray)
            if (doesTrackExist) {
                tracksArray.remove(item)
                tracksArray.add(0, item)
            } else if (tracksArray.size >= SAVED_TRACKS_MAX) {
                tracksArray.remove(tracksArray[SAVED_TRACKS_MAX - 1])
                tracksArray.add(0, item)
            } else {
                tracksArray.add(0, item)
            }
            sharedPreferences.edit()
                .putString(SAVED_TRACKS, createJsonFromTrackList(tracksArray))
                .apply()
        } else {
            val tracksArray: ArrayList<Track> = arrayListOf(item)
            sharedPreferences.edit()
                .putString(SAVED_TRACKS, createJsonFromTrackList(tracksArray))
                .apply()
        }
    }
    override fun returnSavedTracks():ArrayList<Track> {
        val sharedPrefsTracks = sharedPreferences.getString(SAVED_TRACKS,  null)

        return if (sharedPrefsTracks != null) {
            createTrackListFromJson(sharedPrefsTracks)
        } else ArrayList()
    }
    override fun clearSavedTracks() {
        sharedPreferences.edit()
            .putString(SAVED_TRACKS, null)
            .apply()
    }

    private fun doesTrackExist (newTrack: Track, trackList: ArrayList<Track>) : Boolean {
        for (track in trackList ) {
            if (track.trackId == newTrack.trackId) {
                return true
            }
        }
        return false
    }

    private fun createJsonFromTrackList(tracks: ArrayList<Track>): String {
        return Gson().toJson(tracks)
    }

    private fun createTrackListFromJson(json: String?): ArrayList<Track> {
            val gson = Gson()
            val itemType = object : TypeToken<ArrayList<Track>>() {}.type
            return gson.fromJson(json, itemType)
    }

}