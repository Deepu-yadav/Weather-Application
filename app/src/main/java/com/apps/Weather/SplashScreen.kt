package com.apps.Weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class SplashScreen : AppCompatActivity() {
    lateinit var mfusesdlocation:FusedLocationProviderClient
    private var myRequestCode=1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        mfusesdlocation=LocationServices.getFusedLocationProviderClient(this)

        // 1. location permission --> deny
        // 2.location denied through setting
        // 3. gps off


        getLastLocation()

    }



    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if(CheckPermission()){
          if(LocationEnable())  {

           mfusesdlocation.lastLocation.addOnCompleteListener{
               task->
               var location:Location?=task.result
               if(location==null){
                   NewLocation()
               }
               else{
                  Handler(Looper.getMainLooper()).postDelayed({
                      val intent= Intent(this,MainActivity::class.java)
                      intent.putExtra("lat",location.latitude.toString())
                      intent.putExtra("long",location.longitude.toString())
                      startActivity(intent)
                      finish()
                  },2000)
               }
           }
          }
            else{
                Toast.makeText(this,"Please turn on your GPS location ",Toast.LENGTH_LONG).show()
          }
        }
        else{
            RequestPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun NewLocation() {
      var locationRequest= com.google.android.gms.location.LocationRequest()
       locationRequest.priority=LocationRequest.QUALITY_HIGH_ACCURACY
        locationRequest.interval=0
        locationRequest.fastestInterval=0
        locationRequest.numUpdates=1
        mfusesdlocation=LocationServices.getFusedLocationProviderClient(this)
         mfusesdlocation.requestLocationUpdates(locationRequest,locationCallback,Looper.myLooper())
    }
    private val locationCallback= object:LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            var lastlocation: Location? =p0.lastLocation
        }
    }



    private fun CheckPermission(): Boolean {
        if(
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
        )
            return true
        return false


    }
    private fun LocationEnable(): Boolean {
     var locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }
    private fun RequestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION), myRequestCode)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==myRequestCode){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
             getLastLocation()
            }
        }
    }

}
