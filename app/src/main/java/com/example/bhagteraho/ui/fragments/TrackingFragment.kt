package com.example.bhagteraho.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.bhagteraho.R
import com.example.bhagteraho.others.Constants.ACTION_PAUSE_SERVICE
import com.example.bhagteraho.others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.bhagteraho.others.Constants.MAP_ZOOM
import com.example.bhagteraho.others.Constants.POLYLINE_COLOR
import com.example.bhagteraho.others.Constants.POLYLINE_WIDTH
import com.example.bhagteraho.others.TrackingUtility
import com.example.bhagteraho.services.Polyline
import com.example.bhagteraho.services.TrackingService
import com.example.bhagteraho.ui.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {
    private val viewModel: MainViewModel by viewModels()

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()

    /** Google map object for map view it has its own lifecycle which we need to sync with our fragment
     * lifecycle*/
    private  var map:GoogleMap? = null

    private var curTimeInMillis = 0L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapView = view.findViewById<MapView>(R.id.mapView)

        view.findViewById<Button>(R.id.btnToggleRun).setOnClickListener{
            toggelRun()
        }

        /** syncing maps own lifecycle with fragments lifecycle */
        mapView.onCreate(savedInstanceState)


        mapView.getMapAsync{
            map = it
            addAllPolylines()
        }

        subscribeToObservers()
    }

    private fun subscribeToObservers(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })

        TrackingService.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            curTimeInMillis = it
            val formattedTime = TrackingUtility.getFormatedStopWatchTime(curTimeInMillis,true)
            view?.findViewById<TextView>(R.id.tvTimer)?.text = formattedTime
        })
    }

    private fun toggelRun(){
        if(isTracking){
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else{
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun updateTracking(isTracking : Boolean){
        this.isTracking = isTracking
        if(!isTracking){
            view?.findViewById<Button>(R.id.btnToggleRun)?.text  = "Start"
            view?.findViewById<Button>(R.id.btnFinishRun)?.visibility = View.VISIBLE
        }else{
            view?.findViewById<Button>(R.id.btnToggleRun)?.text  = "Stop"
            view?.findViewById<Button>(R.id.btnFinishRun)?.visibility = View.GONE
        }
    }

    private fun moveCameraToUser(){
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun addAllPolylines(){
        for (polyline in pathPoints){
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline(){
        if(pathPoints.isNotEmpty() && pathPoints.last().size>=2){
            val preLastLatLng = pathPoints.last()[pathPoints.last().size-2]
            val lastLastLng = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLastLng)
            map?.addPolyline(polylineOptions)
        }
    }

    /** Starting Service */
    private fun sendCommandToService(action:String) =
        Intent(requireContext(),TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    /** syncing maps own lifecycle with fragments lifecycle */

    override fun onResume() {
        super.onResume()
        view?.findViewById<MapView>(R.id.mapView)?.onResume()
    }

    override fun onStart() {
        super.onStart()
        view?.findViewById<MapView>(R.id.mapView)?.onStart()
    }

    override fun onStop() {
        super.onStop()
        view?.findViewById<MapView>(R.id.mapView)?.onStop()
    }

    override fun onPause() {
        super.onPause()
        view?.findViewById<MapView>(R.id.mapView)?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        view?.findViewById<MapView>(R.id.mapView)?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        view?.findViewById<MapView>(R.id.mapView)?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        view?.findViewById<MapView>(R.id.mapView)?.onSaveInstanceState(outState)
    }

}