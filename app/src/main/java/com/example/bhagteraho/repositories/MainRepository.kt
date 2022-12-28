package com.example.bhagteraho.repositories

import com.example.bhagteraho.db.Run
import com.example.bhagteraho.db.RunDAO
import javax.inject.Inject

class MainRepository @Inject constructor(
   val runDao: RunDAO
) {
    suspend fun insertRun(run:Run) = runDao.insertRun(run)

    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getAllRunsByDate() = runDao.getAllRunsByDate()

    fun getAllRunsByDistance() = runDao.getAllRunsByDistance()

    fun getAllRunsByRunTime() = runDao.getAllRunsByRunTime()

    fun getAllRunsByCaloriesBurned() = runDao.getAllRunsByCaloriesBurned()

    fun getAllRunsBySpeed() = runDao.getAllRunsBySpeed()

    fun getTotalTimeInMilis() = runDao.getTotalTimeInMilis()

    fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()

    fun getTotalDistanceInMeter() = runDao.getTotalDistanceInMeter()

    fun getTotalAverageSpeed() = runDao.getTotalAverageSpeed()

}