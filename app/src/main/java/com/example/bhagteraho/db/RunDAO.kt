package com.example.bhagteraho.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("SELECT * FROM `runs_table` ORDER BY timestamp DESC")
    fun getAllRunsByDate(): LiveData<List<Run>>

    @Query("SELECT * FROM `runs_table` ORDER BY distanceInMeter DESC")
    fun getAllRunsByDistance():LiveData<List<Run>>

    @Query("SELECT * FROM `runs_table` ORDER BY avgspeed DESC")
    fun getAllRunsBySpeed(): LiveData<List<Run>>

    @Query("SELECT * FROM `runs_table` ORDER BY timeInMilis DESC")
    fun getAllRunsByRunTime(): LiveData<List<Run>>

    @Query("SELECT * FROM `runs_table` ORDER BY timestamp DESC")
    fun getAllRunsByCaloriesBurned(): LiveData<List<Run>>

    /* Statistics */

    @Query("SELECT SUM(timeInMilis) FROM runs_table")
    fun getTotalTimeInMilis():LiveData<Long>

    @Query("SELECT SUM(caloriesBurned) FROM runs_table")
    fun getTotalCaloriesBurned():LiveData<Int>

    @Query("SELECT SUM(distanceInMeter) FROM runs_table")
    fun getTotalDistanceInMeter():LiveData<Int>

    @Query("SELECT AVG(avgspeed) FROM runs_table")
    fun getTotalAverageSpeed():LiveData<Float>
}