package com.example.demoandrodibox

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import java.util.*

class LocationTracker(private val mContext: Context) :
        LocationListener {

    // flag for GPS status
    private var isGPSEnabled = false

    // flag for network status
    private var isNetworkEnabled = false

    // flag for GPS status
    private var canGetLocation = false
    private var location // location
            : Location? = null
    private var latitude // latitude
            = 0.0
    private var longitude // longitude
            = 0.0
    private val TAG = "LocationTracker"

    // Declaring a Location Manager
    protected var locationManager: LocationManager? = null

    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {
        try {
            locationManager = mContext
                    .getSystemService(Context.LOCATION_SERVICE) as LocationManager

            // getting GPS status
            isGPSEnabled = locationManager!!
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)

            // getting network status
            isNetworkEnabled = locationManager!!
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                canGetLocation = false
            } else {
                canGetLocation = true
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager!!.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                            this
                    )
                    Log.d("Network", "Network")
                    if (locationManager != null) {
                        location = locationManager!!
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            latitude = location!!.latitude
                            longitude = location!!.longitude
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager!!.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                                this
                        )
                        Log.d("GPS Enabled", "GPS Enabled")
                        if (locationManager != null) {
                            location = locationManager!!
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (location != null) {
                                latitude = location!!.latitude
                                longitude = location!!.longitude
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
        return location
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this@LocationTracker)
        }
    }

    /**
     * Function to get latitude
     */
    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.latitude
        }

        // return latitude
        return latitude
    }

    /**
     * Function to get longitude
     */
    fun getLongitude(): Double {
        if (location != null) {
            longitude = location!!.longitude
        }

        // return longitude
        return longitude
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    fun canGetLocation(): Boolean {
        return canGetLocation
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    fun showSettingsAlert() {
        val alertDialog =
                AlertDialog.Builder(mContext)

        // Setting Dialog Title
        alertDialog.setTitle("Cài đặt GPS")

        // Setting Dialog Message
        alertDialog.setMessage("GPS chưa được kích hoạt. Nhấp vào cài đặt để bật và nhận vị trí, vui lòng khởi động lại ứng dụng sau khi bật GPS")
        alertDialog.setCancelable(false)

        // On pressing Settings button


        alertDialog.setPositiveButton(
                "Cài đặt",
                DialogInterface.OnClickListener { dialog, which ->
                    val intent =
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    mContext.startActivity(intent)
                })


        // Showing Alert Message
        alertDialog.show()
    }

    var rand = Random()
    override fun onLocationChanged(location: Location) {
        //            mapRipple.withNumberOfRipples(3);
        this.location = location
    }

    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {
        location = getLocation()
    }

    override fun onStatusChanged(
            provider: String,
            status: Int,
            extras: Bundle
    ) {
    }

    companion object {
        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 1 // 1 meters

        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES: Long = 1000 // 1 sec
    }

    init {
        getLocation()
    }
}