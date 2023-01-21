package com.example.bhagteraho.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import com.google.android.gms.location.LocationRequest
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.bhagteraho.R
import com.example.bhagteraho.others.Constants.ACTION_PAUSE_SERVICE
import com.example.bhagteraho.others.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.bhagteraho.others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.bhagteraho.others.Constants.ACTION_STOP_SERVICE
import com.example.bhagteraho.others.Constants.FASTEST_LOCATION_INTERVAL
import com.example.bhagteraho.others.Constants.LOCATION_UPDATE_INTERVAL
import com.example.bhagteraho.others.Constants.NOTIFICATION_CHANNEL_ID
import com.example.bhagteraho.others.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.bhagteraho.others.Constants.NOTIFICATION_ID
import com.example.bhagteraho.others.Constants.TIMER_UPDATE_INTERVAL
import com.example.bhagteraho.others.TrackingUtility
import com.example.bhagteraho.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

/** Inheriting Lifecycleservice to tell the observer function of live data object the owner of the life cycle*/
class TrackingService : LifecycleService() {

    var isFirstRun = true

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val timeRunInSeconds = MutableLiveData<Long>()

    companion object{
        val timeRunInMillis = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
    }

    private fun postInitialValues(){
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        isTracking.observe(this, Observer {
         updateLocationTracking(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent.let {
            if (it != null) {
                when(it.action){
                    ACTION_START_OR_RESUME_SERVICE->{
                        if(isFirstRun){
                            startForeGroundService()
                            isFirstRun = false
                        }else{
                            Timber.d("Resuming Service")
                            startTimer()
                        }
                    }
                    ACTION_PAUSE_SERVICE->{
                        Timber.d("Paused service")
                        pauseService()
                    }
                    ACTION_STOP_SERVICE->{
                        Timber.d("Stopped service")
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimeStamp = 0L

    private fun startTimer(){
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            while(isTracking.value!!){
                lapTime = System.currentTimeMillis() - timeStarted
                timeRunInMillis.postValue(timeRun+lapTime)
                if(timeRunInMillis.value!!>=lastSecondTimeStamp+1000L){
                    timeRunInSeconds.postValue(timeRunInSeconds.value!!+1)
                    lastSecondTimeStamp += 1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking:Boolean){
        if(isTracking){
            if(TrackingUtility.hasLocationPermissions(this)){
                val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,
                    LOCATION_UPDATE_INTERVAL)
                    .setWaitForAccurateLocation(false)
                    .setMinUpdateIntervalMillis(FASTEST_LOCATION_INTERVAL)
                    .setMaxUpdateDelayMillis(LOCATION_UPDATE_INTERVAL)
                    .build()

                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }else{
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    val locationCallback = object : LocationCallback(){
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value!!){
                result?.locations?.let { locations->
                    for (curLocation in locations){
                        addPathPoints(curLocation)
                        Timber.d("NEW LOCATION: ${curLocation.latitude},${curLocation.longitude}")
                    }
                }
            }
        }
    }

    private fun addPathPoints(location: Location?){
        location?.let {
            val pos = LatLng(location.latitude,location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun startForeGroundService(){

        startTimer()
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_baseline_assistant_direction_24)
            .setContentTitle("BhagteRaho")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID,notificationBuilder.build())
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this,MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }
}