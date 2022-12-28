package com.example.bhagteraho.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "runs_table")
data class Run(
    var previewimg: Bitmap? = null,
    var timestamp: Long = 0L,
    var avgspeed : Float = 0f,
    var distanceInMeter : Int = 0,
    var timeInMilis: Long = 0L,
    var caloriesBurned: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var key:Int? = null
}