package com.practicum.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.library.data.db.PlaylistsTracksEntity
import com.practicum.playlistmaker.library.data.db.TrackEntity

@Dao
interface PlaylistsTracksDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistsTracksEntity)

    @Query("DELETE FROM playlists_tracks_table WHERE track_id = :trackId")
    fun deleteTrackById(trackId: Long)

    @Query("SELECT * FROM playlists_tracks_table")
    suspend fun getPlaylistsTracks(): List<PlaylistsTracksEntity>
}