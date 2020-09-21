package com.example.demoandrodibox

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.arsy.maps_library.MapRipple
import com.example.demoandrodibox.model.BaseResponse
import com.example.demoandrodibox.model.RequestBody
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainActivity : FragmentActivity(), OnMapReadyCallback{

    private var locationTrackObj: LocationTracker? = null
    private var mapRipple: MapRipple? = null
    private val MY_PERMISSIONS_REQUEST_FINE_LOCATION = 100
    private var latLng =
        LatLng(21.024673, 105.789692)
    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationTrackObj = LocationTracker(this)
        if (!locationTrackObj?.canGetLocation()!!) {
            locationTrackObj?.showSettingsAlert()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission()
            }
        }
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        startService(Intent(this, MyServices::class.java))
//        startService(Intent(this, UpdateService::class.java))
//        getListLocation()
        getLocations()
    }

    private fun getBookCar() {
        ApiService.service.getLocation().subscribeOn(Schedulers.io()) //(*)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<BaseResponse> {
                    override fun onSuccess(respon: BaseResponse) {
                        // xu ly ve anh dg di o day.
                        if (respon.lstCarPosInfos != null && respon.lstCarPosInfos!!.isNotEmpty()) {
//                            mMap?.clear()
                            createMarker(respon.lstCarPosInfos!![0].latitude,
                                    respon.lstCarPosInfos!![0].longtitude, R.drawable.transport)

                        }
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {
                    }
                })
    }

    private fun getListLocation() {
        ApiService.service.getLocation().subscribeOn(Schedulers.io()) //(*)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<BaseResponse> {
                    override fun onSuccess(respon: BaseResponse) {
                        // xu ly ve anh dg di o day.
                        if (respon.lstCarPosInfos != null && respon.lstCarPosInfos!!.isNotEmpty()) {

                            for (item in respon.lstCarPosInfos!!) {
                                createMarker(item.latitude, item.longtitude, getColor(item.color))
                            }

                        }
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {
                        Toast.makeText(this@MainActivity, "Co loi xay ra", Toast.LENGTH_LONG).show()
                    }

                })
    }

    private fun getColor(code : String?): Int{
        var mColor = 0
        when(code){
            "black" ->{
                mColor = R.drawable.bg_black
            }
            "darker_gray" ->{
                mColor = R.drawable.bg_gray
            }
            "holo_blue_dark" ->{
                mColor = R.drawable.bg_bule
            }
            "holo_green_dark" ->{
                mColor = R.drawable.bg_green
            }
            "holo_orange_dark" ->{
                mColor = R.drawable.bg_orange
            }
            "holo_red_dark" ->{
                mColor = R.drawable.bg_red
            }
        }
        return mColor
    }

    private fun createMarker(latitude: Double?, longitude: Double?, iconResID: Int): Marker? {
        return mMap?.addMarker(latitude?.let { longitude?.let { it1 -> LatLng(it, it1) } }?.let {
            locationTrackObj?.getLocation()?.bearing?.let { it1 ->
                MarkerOptions()
                        .position(it)
                        .anchor(0.5f, 0.5f)
                        .icon(CommonUtil.bitmapDescriptorFromVector(this, iconResID))
                        .flat(true).anchor(0.5f,0.5f).rotation(it1)
            }
        })
    }



    @SuppressLint("MissingPermission")
    private fun initializeMap(mMap: GoogleMap?) {
        if (mMap != null) {
            mMap.isMyLocationEnabled = true

            var location = mMap.myLocation
            if (location == null) location = locationTrackObj?.getLocation()
            try {
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            location.latitude,
                            location.longitude
                        ), 14f
                    )
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            if (location != null) latLng = LatLng(
                location.latitude,
                location.longitude
            ) else {
                latLng = LatLng(0.0, 0.0)
            }
            mapRipple =
                MapRipple(mMap, latLng, this)

//            mapRipple.withNumberOfRipples(3);
            mapRipple?.withFillColor(
                Color.parseColor(
                    "#FFA3D2E4"
                )
            )
            mapRipple?.withDistance(3000.0) // 2000 metres radius
            mapRipple?.withRippleDuration(12000) //12000ms
            mapRipple?.withTransparency(0.5f)
            mapRipple?.startRippleMapAnimation()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun checkLocationPermission(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION
                )
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION
                )
            }
            false
        } else {
            true
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.mMap = googleMap
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                initializeMap(mMap)
            }
        } else {
            initializeMap(mMap)
        }
    }

    override fun onStop() {
        super.onStop()
        locationTrackObj?.stopUsingGPS()
        try {
            if (mapRipple?.isAnimationRunning!!) {
                mapRipple?.stopRippleMapAnimation()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, MyServices::class.java))
//        stopService(Intent(this, UpdateService::class.java))
        unregisterReceiver(broadcastReceiver)
//        unregisterReceiver(broadcastReceiver)
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
//            Log.e("Tag: ", locationTrackObj?.getLocation()?.bearingTo(latLng))

//            getBookCar()
            getLocations()
        }
    }
    override fun onResume() {
        super.onResume()
//        registerReceiver(broadcastReceiver, IntentFilter(
//                UpdateService.BROADCAST_ACTION))
        registerReceiver(broadcastReceiver, IntentFilter(
                MyServices.MyServices_BROADCAST_ACTION))
    }

    private fun getLocations() {
        val body = RequestBody()
        body.latitude = locationTrackObj?.getLatitude()
        body.longtitude = locationTrackObj?.getLongitude()
//        body.posTime = setDataToday()
        // 1 duong bang phang
        // 2 duong doi nui
        // 3 dong du cu
        body.topoType = 3
        if (TextUtils.isEmpty(Build.MODEL)) {
            body.deviceName = "MT6572"
        } else {
            body.deviceName = Build.MODEL
        }
        ApiService.service.savePos(body).subscribeOn(Schedulers.io()) //(*)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<BaseResponse> {
                    override fun onSuccess(t: BaseResponse) {

                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {}

                })
    }

}

