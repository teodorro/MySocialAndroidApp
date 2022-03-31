package com.example.mysocialandroidapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mysocialandroidapp.entity.JobEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {
    @Query("SELECT * FROM JobEntity ORDER BY start DESC")
    fun getAll(): Flow<List<JobEntity>>

    @Query("SELECT * FROM JobEntity WHERE id = :id")
    fun getById(id: Long): JobEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(job: JobEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(jobs: List<JobEntity>)

    @Query("DELETE FROM JobEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("DELETE FROM JobEntity")
    suspend fun removeAll()
}