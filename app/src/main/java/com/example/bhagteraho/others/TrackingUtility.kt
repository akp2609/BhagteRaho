package com.example.bhagteraho.others

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import java.util.concurrent.TimeUnit

object TrackingUtility {
    fun hasLocationPermissions(context: Context):Boolean{
        var ans = false
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

        ans = (ContextCompat.checkSelfPermission(
                      context,
                      Manifest.permission.ACCESS_FINE_LOCATION) ==
              PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                PackageManager.PERMISSION_GRANTED)

    } else {

            ans = (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED)

    }
    return ans
    }

    fun getFormatedStopWatchTime(miliSecond:Long,includeMilis:Boolean = false):String{
        val hours = TimeUnit.MILLISECONDS.toHours(miliSecond)
        val minute = TimeUnit.MILLISECONDS.toMinutes(
            miliSecond - TimeUnit.HOURS.toMillis(hours)
        )
        val seconds = TimeUnit.MILLISECONDS.toSeconds(
            miliSecond - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minute)
        )

        return if(!includeMilis){
            "${if(hours<10) "0" else ""}$hours:" +
                    "${if (minute<10) "0" else ""}$minute:" +
                    "${if (seconds<10) "0" else ""}$seconds"
        }else{
            var mili = miliSecond - TimeUnit.HOURS.toMillis(hours) -
                    TimeUnit.MINUTES.toMillis(minute)- TimeUnit.SECONDS.toMillis(seconds)
            mili /= 10
            "${if(hours<10) "0" else ""}$hours:" +
                    "${if (minute<10) "0" else ""}$minute:" +
                    "${if (seconds<10) "0" else ""}$seconds:" +
                    "${if(mili < 10) "0" else ""}$mili"
        }
    }


}