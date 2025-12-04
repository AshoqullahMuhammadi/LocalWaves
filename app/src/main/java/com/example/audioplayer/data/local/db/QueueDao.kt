package com.example.audioplayer.data.local.db

import androidx.room.*
import com.example.audioplayer.data.model.QueueItem
import com.example.audioplayer.data.model.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface QueueDao {
    @Query("""
        SELECT t.* FROM tracks t 
        INNER JOIN queue_items qi ON t.id = qi.trackId 
        ORDER BY qi.position ASC
    """)
    fun getQueueTracks(): Flow<List<Track>>
    
    @Query("SELECT * FROM queue_items ORDER BY position ASC")
    fun getAllQueueItems(): Flow<List<QueueItem>>
    
    @Query("SELECT * FROM queue_items ORDER BY position ASC")
    suspend fun getAllQueueItemsSync(): List<QueueItem>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueueItem(item: QueueItem)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueueItems(items: List<QueueItem>)
    
    @Delete
    suspend fun deleteQueueItem(item: QueueItem)
    
    @Query("DELETE FROM queue_items WHERE trackId = :trackId")
    suspend fun removeTrackFromQueue(trackId: Long)
    
    @Query("DELETE FROM queue_items")
    suspend fun clearQueue()
    
    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM queue_items")
    suspend fun getNextPosition(): Int
    
    @Query("SELECT COUNT(*) FROM queue_items")
    suspend fun getQueueSize(): Int
    
    @Transaction
    suspend fun addToQueue(trackId: Long) {
        val position = getNextPosition()
        insertQueueItem(QueueItem(trackId = trackId, position = position))
    }
    
    @Transaction
    suspend fun addToQueueNext(trackId: Long, afterPosition: Int) {
        // Shift existing items
        val items = getAllQueueItemsSync()
        items.filter { it.position > afterPosition }.forEach { item ->
            insertQueueItem(item.copy(position = item.position + 1))
        }
        insertQueueItem(QueueItem(trackId = trackId, position = afterPosition + 1))
    }
    
    @Transaction
    suspend fun replaceQueue(trackIds: List<Long>) {
        clearQueue()
        trackIds.forEachIndexed { index, trackId ->
            insertQueueItem(QueueItem(trackId = trackId, position = index))
        }
    }
    
    @Transaction
    suspend fun moveItem(fromPosition: Int, toPosition: Int) {
        val items = getAllQueueItemsSync().toMutableList()
        if (fromPosition < 0 || fromPosition >= items.size || toPosition < 0 || toPosition >= items.size) return
        
        val item = items.removeAt(fromPosition)
        items.add(toPosition, item)
        
        clearQueue()
        items.forEachIndexed { index, queueItem ->
            insertQueueItem(queueItem.copy(position = index))
        }
    }
}

