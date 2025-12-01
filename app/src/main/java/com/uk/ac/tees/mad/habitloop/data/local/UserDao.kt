package com.uk.ac.tees.mad.habitloop.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Upsert
    suspend fun upsertUser(user: UserEntity)

    @Query("SELECT * FROM user WHERE uid = :userId")
    fun getUser(userId: String): Flow<UserEntity?>

    @Query("DELETE FROM user")
    suspend fun clearUsers()
}
