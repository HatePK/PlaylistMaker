package com.practicum.playlistmaker.library.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.practicum.playlistmaker.library.data.db.PlaylistsTracksEntity

@Dao
interface PlaylistsTracksDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistsTracksEntity)
}