package com.example.demoandrodibox

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.arsy.maps_library.MapRipple
import com.example.demoandrodibox.model.BaseResponse
import com.example.demoandrodibox.model.RequestBody
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MyServices : Service() {
    private var locationTrackObj: LocationTracker? = null
    var mHandler = Handler() //run on another Thread to avoid crash
    private var mTimer: Timer? = null //timer handling
//    private var mapRipple: MapRipple? = null
//    private var latLng =
//            LatLng(21.024673, 105.789692)
//    private var mMap: GoogleMap? = null
    private var intent: Intent? = null


    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
//        locationTrackObj = LocationTracker(this)
        if (mTimer != null) // Cancel if already existed
            mTimer!!.cancel() else mTimer = Timer() //recreate new
        mTimer!!.scheduleAtFixedRate(TimeDisplay(), 0, notify.toLong()) //Schedule task
        intent = Intent(MyServices_BROADCAST_ACTION)
    }


    override fun onDestroy() {
        super.onDestroy()
        mTimer!!.cancel() //For Cancel Timer
        Toast.makeText(this, "Service is Destroyed", Toast.LENGTH_SHORT).show()
    }

//    @SuppressLint("MissingPermission")
//    private fun initializeMap(mMap: GoogleMap?) {
//        if (mMap != null) {
//            mMap.isMyLocationEnabled = true
//            var location = mMap.myLocation
//            if (location == null) location = locationTrackObj!!.getLocation()
//            try {
//                mMap.animateCamera(
//                        CameraUpdateFactory.newLatLngZoom(
//                                LatLng(
//                                        location.latitude,
//                                        location.longitude
//                                ), 14f
//                        )
//                )
//            } catch (e: java.lang.Exception) {
//                e.printStackTrace()
//            }
//            if (location != null) latLng = LatLng(
//                    location.latitude,
//                    location.longitude
//            ) else {
//                latLng = LatLng(0.0, 0.0)
//            }
//            mapRipple =
//                    MapRipple(mMap, latLng, this)
//
////            mapRipple.withNumberOfRipples(3);
//            mapRipple?.withFillColor(
//                    Color.parseColor(
//                            "#FFA3D2E4"
//                    )
//            )
//            mapRipple?.withDistance(3000.0) // 2000 metres radius
//            mapRipple?.withRippleDuration(12000) //12000ms
//            mapRipple?.withTransparency(0.5f)
//            mapRipple?.startRippleMapAnimation()
//        }
//    }
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        this.mMap = googleMap
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                initializeMap(mMap)
//            }
//        } else {
//            initializeMap(mMap)
//        }
//    }
//
//    private fun getLocation() {
//        val body = RequestBody()
//        body.latitude = latLng.latitude
//        body.longtitude = latLng.longitude
////        body.posTime = setDataToday()
//        // 1 duong bang phang
//        // 2 duong doi nui
//        // 3 dong du cu
//        body.topoType = 3
//        if (TextUtils.isEmpty(Build.MODEL)) {
//            body.deviceName = "MT6572"
//        } else {
//            body.deviceName = Build.MODEL
//        }
//        ApiService.service.savePos(body).subscribeOn(Schedulers.io()) //(*)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : SingleObserver<BaseResponse> {
//                    override fun onSuccess(t: BaseResponse) {
//
//                    }
//
//                    override fun onSubscribe(d: Disposable) {}
//
//                    override fun onError(e: Throwable) {}
//
//                })
//    }

    //class TimeDisplay for handling task
    internal inner class TimeDisplay : TimerTask() {
        override fun run() {
            // run on another thread
            mHandler.post { // display toast
                sendBroadcast(intent)

//                mMap?.let { onMapReady(it) }
//                getLocation()

            }
        }
    }



    companion object {
        const val MyServices_BROADCAST_ACTION = "com.mukesh.MyServices";
        const val notify = 12000 //interval between two services(Here Service run every 5 Minute)
    }
}