package com.example.shockapp

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocationPermission(onPermissionGranted: @Composable ()->Unit){
    val permissionsState= rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    if(permissionsState.allPermissionsGranted){
        onPermissionGranted()
    }else{
        LaunchedEffect(Unit) {
            permissionsState.launchMultiplePermissionRequest()

        }
    }
}

@SuppressLint("MissingPermission")
suspend fun getCurrentLocation(context: Context):Location?{
    return try{
        val fusedLocationClient:FusedLocationProviderClient=
            LocationServices.getFusedLocationProviderClient(context)
         fusedLocationClient.lastLocation.await()
    }catch (e:Exception){
        Log.e("LocationError", "Error fetching location: ${e.message}")
        null
    }

}