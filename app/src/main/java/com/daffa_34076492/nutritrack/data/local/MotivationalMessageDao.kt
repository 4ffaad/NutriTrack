package com.daffa_34076492.nutritrack.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.daffa_34076492.nutritrack.model.MotivationalMessage

@Dao
interface MotivationalMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MotivationalMessage)

    @Query("SELECT * FROM motivational_messages WHERE userId = :userId")
    suspend fun getMessagesForUser(userId: Int): List<MotivationalMessage>

    @Query("DELETE FROM motivational_messages WHERE userId = :userId")
    suspend fun clearMessagesForUser(userId: Int)

}

