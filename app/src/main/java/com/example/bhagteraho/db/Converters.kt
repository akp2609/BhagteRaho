package com.example.bhagteraho.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

/** Type Converters for RunningDataBase to save images of the maps*/
class Converters {

    @TypeConverter
    fun toBitmap(Byte:ByteArray):Bitmap{
        return BitmapFactory.decodeByteArray(Byte,0,Byte.size)
    }

    @TypeConverter
    fun toByteArray(bmp:Bitmap):ByteArray{
        var outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG,100,outputStream)
        return outputStream.toByteArray()
    }
}