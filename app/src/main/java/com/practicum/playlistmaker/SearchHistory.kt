package com.practicum.playlistmaker

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken



class SearchHistory (context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
    private val sharedPrefsTracks = sharedPreferences.getString(SAVED_TRACKS, null)

    fun onListElementClick(item: Track) {
        if (sharedPrefsTracks != null) {
            val tracksArray = createTrackListFromJson(sharedPrefsTracks)
            val doesTrackExist = doesTrackExist(item, tracksArray)
            if (doesTrackExist) {
                tracksArray.remove(item)
                tracksArray.add(0, item)
            } else if (tracksArray.size >= 10) {
                tracksArray.remove(tracksArray[9])
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

    private fun doesTrackExist (newTrack: Track, trackList: ArrayList<Track>) : Boolean {
        for (track in trackList ) {
            if (track.trackId == newTrack.trackId) {
                return true
            }
        }
        return false
    }

    fun returnSavedTracks():ArrayList<Track> {
        return if (sharedPrefsTracks != null) {
            createTrackListFromJson(sharedPrefsTracks)
        } else ArrayList()
    }

    fun clearSavedTracks() {
        sharedPreferences.edit()
            .putString(SAVED_TRACKS, null)
            .apply()
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