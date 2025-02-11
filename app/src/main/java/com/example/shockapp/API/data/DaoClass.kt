package com.example.shockapp.API.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(searchHistory: SearchHistory): Long

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 5")
    fun getSearchHistory(): Flow<List<SearchHistory>>

    @Query("DELETE FROM search_history WHERE id IN (SELECT id FROM search_history ORDER BY timestamp ASC LIMIT (SELECT COUNT(*) - 5 FROM search_history))")
    suspend fun trimSearchHistory()

    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteSearchHistory(id: Int)

    @Query("SELECT COUNT(*) FROM search_history")
    suspend fun getHistoryCount(): Int

    @Query("DELETE FROM search_history WHERE id IN (SELECT id FROM search_history ORDER BY timestamp ASC LIMIT :limit)")
    suspend fun trimOldEntries(limit: Int)

}