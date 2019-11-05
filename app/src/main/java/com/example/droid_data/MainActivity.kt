package com.example.droid_data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Handler
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    private val requestCode= 1000
    private var uniqueUserId= 1
    private var acceleration= ""
    private var rotation= ""
    private var gravity= ""
    private var magnet= ""
    private var latitude= ""
    private var longitude= ""
    private var speed= ""

    private var date = Date();
    private val formatter1 = SimpleDateFormat("dd-mm-yyyy")
    private val formatter2 = SimpleDateFormat("HH:mm")
    private val currentDate: String = formatter1.format(date)
    private val currentTime: String = formatter2.format(date)


    private val handler = Handler()
    private val runnable = object : Runnable {
        override fun run() {
            saveData()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {

        when (event?.sensor?.type) {

            Sensor.TYPE_ACCELEROMETER -> {
                acceleration = "x = ${event.values[0]} m/s2\n" +
                        "y = ${event.values[1]} m/s2\n" +
                        "z = ${event.values[2]} m/s2"
                accDta.text = acceleration
                //saveData()
            }

            Sensor.TYPE_GYROSCOPE -> {
                rotation = "x = ${event.values[0]} rad/s\n" +
                        "y = ${event.values[1]} rad/s \n" +
                        "z = ${event.values[2]} rad/s"
                rotDta.text = rotation
                //saveData()
            }
            Sensor.TYPE_GRAVITY -> {
                gravity = "x = ${event.values[0]} m/s\n" +
                        "y = ${event.values[1]} m/s \n" +
                        "z = ${event.values[2]} m/s"
                gravDta.text = gravity
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                magnet = "x = ${event.values[0]} T\n" +
                        "y = ${event.values[1]} T \n" +
                        "z = ${event.values[2]} T"
                magDta.text = magnet
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Sets the user id
        usrId.text= uniqueUserId.toString()

        sensorManager= getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_NORMAL
        )

        /* Check Permissions For Location Services*/
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCode)
        }else{
            buildLocationRequest()
            buildLocationCallBack()

            /* Create FusedProviderClient */
            fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)
            /* Set Location*/
            /* ADD BTN */
            btnUpdates.setOnClickListener(View.OnClickListener {
                if (ActivityCompat.checkSelfPermission(this@MainActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this@MainActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCode)
                    return@OnClickListener
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback, Looper.myLooper())

                handler.postDelayed(runnable, 1000)

                /* Change State of button*/
                btnUpdates.isEnabled= !btnUpdates.isEnabled
            })
        }
    }

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                /* Get Last Location*/
                val location = p0!!.locations.get(p0!!.locations.size - 1)
                latitude = location.latitude.toString()
                longitude = location.longitude.toString()
                speed = location.speed.toString()
                gpsDta.text = "Latitude: " + latitude + "º\n" +
                        "Longitude: " + longitude + "º\n" + "Speed: " +
                        speed + "m/s\n"
            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest= LocationRequest()
        locationRequest.priority= LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval= 5000
        locationRequest.fastestInterval= 3000
        locationRequest.smallestDisplacement= 10f
    }

    private fun saveData() {
        val userId = uniqueUserId.toString()

        val ref = FirebaseDatabase.getInstance().getReference("datalines")
        val dataLineId = ref.push().key.toString()

        val dataLine = DataLine(dataLineId, userId, acceleration, rotation, latitude,
            longitude, speed, currentDate, currentTime)

        ref.child(dataLineId).setValue(dataLine)
    }


}