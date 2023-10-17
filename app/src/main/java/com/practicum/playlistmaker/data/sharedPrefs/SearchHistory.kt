package com.practicum.playlistmaker.data.sharedPrefs

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.ui.search.SAVED_TRACKS
import com.practicum.playlistmaker.ui.settings.SHARED_PREFERENCES


class SearchHistory (context: Context) {
    companion object {
        const val SAVED_TRACKS_MAX = 10
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        SHARED_PREFERENCES, MODE_PRIVATE)
    private val sharedPrefsTracks = sharedPreferences.getString(SAVED_TRACKS, null)

    fun onListElementClick(item: TrackDto) {
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
            val tracksArray: ArrayList<TrackDto> = arrayListOf(item)
            sharedPreferences.edit()
                .putString(SAVED_TRACKS, createJsonFromTrackList(tracksArray))
                .apply()
        }
    }

    private fun doesTrackExist (newTrack: TrackDto, trackList: ArrayList<TrackDto>) : Boolean {
        for (track in trackList ) {
            if (track.trackId == newTrack.trackId) {
                return true
            }
        }
        return false
    }

    fun returnSavedTracks():ArrayList<TrackDto> {
        return if (sharedPrefsTracks != null) {
            createTrackListFromJson(sharedPrefsTracks)
        } else ArrayList()
    }

    fun clearSavedTracks() {
        sharedPreferences.edit()
            .putString(SAVED_TRACKS, null)
            .apply()
    }

    private fun createJsonFromTrackList(tracks: ArrayList<TrackDto>): String {
        return Gson().toJson(tracks)
    }

    private fun createTrackListFromJson(json: String?): ArrayList<TrackDto> {
            val gson = Gson()
            val itemType = object : TypeToken<ArrayList<TrackDto>>() {}.type
            return gson.fromJson(json, itemType)
    }

}