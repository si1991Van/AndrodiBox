package com.example.demoandrodibox

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import java.util.*

class UpdateService : Service() {

//    var mMap: GoogleMap? = null
    var mHandler = Handler() //run on another Thread to avoid crash
    private var mTimer: Timer? = null //timer handling
    private var callback: UpdateIconCallback?= null
    private var intente: Intent? = null
    companion object {
        const val BROADCAST_ACTION = "com.mukesh.service";
    }

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }
    override fun onCreate() {
        if (mTimer != null) // Cancel if already existed
            mTimer!!.cancel() else mTimer = Timer() //recreate new
        mTimer!!.scheduleAtFixedRate(UpdateBookCar(), 0, MyServices.notify.toLong()) //Schedule task
        intente = Intent(BROADCAST_ACTION)
    }

    override fun onDestroy() {
        super.onDestroy()
        mTimer!!.cancel() //For Cancel Timer
        Toast.makeText(this, "Service is Destroyed", Toast.LENGTH_SHORT).show()
    }


    internal  inner class UpdateBookCar: TimerTask() {
        override fun run() {
            mHandler.post{
                sendBroadcast(intente);
            }
        }

    }


}