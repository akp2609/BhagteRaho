package com.example.bhagteraho.ui.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.bhagteraho.R
import com.example.bhagteraho.ui.viewmodels.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run) {

    private var context: Fragment? = null

    init {
        this.context = context
    }
    /** Setting permission variables from run fragment for location */
    private lateinit var permissionlauncher:ActivityResultLauncher<Array<String>>
    private var isFineLocationPermissionGranted = false
    private var isCoarseLocationPermissionGranted = false
    private var isBackgroundLocationPermissionGranted = false

    private var permissionList:MutableList<String> = ArrayList()

    private val viewModel: MainViewModel by viewModels()

    lateinit var fab : FloatingActionButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkpermissions()
        requestpermissions()

        fab = view.findViewById(R.id.fab)
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }
    }

    private fun checkpermissions(){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q)
        { permissionlauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
                    permissions->
                isFineLocationPermissionGranted =
                    permissions[Manifest.permission.ACCESS_FINE_LOCATION]?:isFineLocationPermissionGranted
                isCoarseLocationPermissionGranted =
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION]?:isCoarseLocationPermissionGranted
                isBackgroundLocationPermissionGranted =
                    permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION]?:isBackgroundLocationPermissionGranted
            }
        }
        else{
        permissionlauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
                permissions->
                    isFineLocationPermissionGranted =
                        permissions[Manifest.permission.ACCESS_FINE_LOCATION]?:isFineLocationPermissionGranted
                    isCoarseLocationPermissionGranted =
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION]?:isCoarseLocationPermissionGranted
            }
        }
    }

    private fun requestpermissions(){

        addpermissions(Manifest.permission.ACCESS_COARSE_LOCATION,requireActivity())
        addpermissions(Manifest.permission.ACCESS_FINE_LOCATION,requireActivity())
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            addpermissions(Manifest.permission.ACCESS_BACKGROUND_LOCATION,requireActivity())
        }

        if(permissionList.isNotEmpty()){
            permissionlauncher.launch(permissionList.toTypedArray())
        }
    }

    private fun addpermissions(perm:String,context: Activity){

        when{
            ContextCompat.checkSelfPermission(
                requireContext(),perm)
                    == PackageManager.PERMISSION_GRANTED ->{
                        Log.d("TAG","Permission Granted")
                    }
            ActivityCompat.shouldShowRequestPermissionRationale(
                context,
                perm
            )->{
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage("Your Device location is required for using this application please go to" +
                        "Settings->Permissions->BhagteRaho and enable location permissions")
                    .setCancelable(false)
                    .setPositiveButton("OK") { dialog, id ->
                        dialog.cancel()
                        permissionList.add(perm)
                    }
                    .setNegativeButton("Cancel") { dialog, id ->
                        dialog.cancel()
                    }
            }
            else->{
                permissionList.add(perm)
            }
        }
    }


}