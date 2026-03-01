package com.example.kotlin_app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey val symbol: String
)