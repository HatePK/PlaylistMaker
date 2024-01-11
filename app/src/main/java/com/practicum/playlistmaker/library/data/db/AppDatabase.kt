package com.practicum.playlistmaker.library.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.library.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.library.data.db.dao.PlaylistsTracksDao
import com.practicum.playlistmaker.library.data.db.dao.TrackDao

@Database(version = 5, entities = [TrackEntity::class, PlaylistEntity::class, PlaylistsTracksEntity::class])
abstract class AppDatabase: RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistsTracksDao(): PlaylistsTracksDao

}

