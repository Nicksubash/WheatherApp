package com.example.shockapp.API.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val query: String,
    val timestamp: Long = System.currentTimeMillis()
)
