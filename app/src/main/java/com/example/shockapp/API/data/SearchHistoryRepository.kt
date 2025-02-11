package com.example.shockapp.API.data

import android.util.Log
import kotlinx.coroutines.flow.Flow

class SearchHistoryRepository(private val dao: SearchHistoryDao) {
    // Expose the history as a Flow for reactive updates.
    val searchHistory: Flow<List<SearchHistory>> = dao.getSearchHistory()

    suspend fun addSearchQuery(query: String) {
        Log.d("SearchHistory", "Inserting query: $query") // Debug log
        dao.insert(SearchHistory(query = query))
    }

    suspend fun trimSearchHistory() {
        val count = dao.getHistoryCount()  // Get the total count
        if (count > 5) {
            dao.trimOldEntries(count - 5)  // Trim only if needed
        }
    }


    suspend fun deleteHistoryItem(history: SearchHistory) {
        dao.deleteSearchHistory(history.id)
    }

}
