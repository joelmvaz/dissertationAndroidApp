package com.example.droid_data

import android.Manifest
import android.annotation.SuppressLint
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
import android.util.Log
import android.widget.EditText
import androidx.core.view.isInvisible
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var uniqueUserId: EditText //Variable used to store user id
    private val requestCode= 1000
    private var acceleration= ""
    private var accelerationX= 0.0
    private var accelerationY= 0.0
    private var accelerationZ= 0.0
    private var rotation= ""
    private var rotationX= 0.0
    private var rotationY= 0.0
    private var rotationZ= 0.0
    private var gravity= ""
    private var magnet= ""
    private var latitude= 0.0
    private var longitude= 0.0
    private var speed= 0.0
    @SuppressLint("SimpleDateFormat")
    private val formatter1 = SimpleDateFormat("dd-MM-yyyy")
    @SuppressLint("SimpleDateFormat")
    private val formatter2 = SimpleDateFormat("HH:mm:ss")
    private var currentDate= ""
    private var currentTime= ""
    private var userId= ""
    private var tripId = 1
    private val handler = Handler()
    
    private val runnable = object : Runnable {
        override fun run() {
            val date = Date()
            currentDate = formatter1.format(date)
            currentTime = formatter2.format(date)
            saveData()
            handler.postDelayed(this, 3000)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {

        when (event?.sensor?.type) {

            Sensor.TYPE_ACCELEROMETER -> {
                acceleration = "x = ${event.values[0]} m/s2\n" +
                        "y = ${event.values[1]} m/s2\n" +
                        "z = ${event.values[2]} m/s2"
                accelerationX = event.values[0].toDouble()
                accelerationX = Math.round(accelerationX * 1000.0)/1000.0
                accelerationY = event.values[1].toDouble()
                accelerationY = Math.round(accelerationY * 1000.0)/1000.0
                accelerationZ = event.values[2].toDouble()
                accelerationZ = Math.round(accelerationZ * 1000.0)/1000.0
                accDta.text = acceleration
            }

            Sensor.TYPE_GYROSCOPE -> {
                rotation = "x = ${event.values[0]} rad/s\n" +
                        "y = ${event.values[1]} rad/s \n" +
                        "z = ${event.values[2]} rad/s"
                rotationX = event.values[0].toDouble()
                rotationX = Math.round(rotationX * 1000.0)/1000.0
                rotationY = event.values[1].toDouble()
                rotationY = Math.round(rotationY * 1000.0)/1000.0
                rotationZ = event.values[2].toDouble()
                rotationZ = Math.round(rotationZ * 1000.0)/1000.0
                rotDta.text = rotation
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

        uniqueUserId= findViewById(R.id.usrId)
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

                //Save user Input id
                userId= uniqueUserId.text.toString()
                println(userId)

                /* Change State of button*/
                btnUpdates.isEnabled= false
                btnUpdates.isInvisible= true
                usrId.isEnabled= false
                getDataPath()
            })
        }
    }

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                /* Get Last Location*/
                val location = p0!!.locations.get(p0!!.locations.size - 1)
                latitude = location.latitude.toDouble()
                longitude = location.longitude.toDouble()
                speed = location.speed.toDouble()
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

    private fun getDataPath(){
        //if ID is empty
        if(userId == ""){
            userId = "00"
        }

        var check = 0

        val ref = FirebaseDatabase.getInstance().getReference(userId)

        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0!!.exists()){
                    var trip = p0.children.count()
                    if (check == 0){
                        tripId = trip + 1
                        check = 1
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

    }

    private fun saveData() {

        val refDriver = FirebaseDatabase.getInstance().getReference(userId + "/" +tripId)

        val dataLineId = refDriver.push().key.toString()

        val dataLine = DataLine(//dataLineId, userId,
            accelerationX, accelerationY, accelerationZ,
            rotationX, rotationY, rotationZ,
            latitude, longitude,
            //speed,
            currentDate, currentTime)

        refDriver.child(dataLineId).setValue(dataLine)


    }
}