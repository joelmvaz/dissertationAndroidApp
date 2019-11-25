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
    private var checkTrip = false
    private val handler = Handler()
    //initiate classes to calculate average values
    private var accelerationX_aver = CalculateAverage()
    private var accelerationY_aver = CalculateAverage()
    private var accelerationZ_aver = CalculateAverage()
    private var rotationX_aver = CalculateAverage()
    private var rotationY_aver = CalculateAverage()
    private var rotationZ_aver = CalculateAverage()
    //calibration
    private var calibration = Calibration(accelerationX_aver, accelerationY_aver, accelerationZ_aver, rotationX_aver, rotationY_aver, rotationZ_aver)

    private val runnable = object : Runnable {
        override fun run() {
            val date = Date()
            currentDate = formatter1.format(date)
            currentTime = formatter2.format(date)

            if(!checkTrip){
                saveData("starts")
            }
            else{
                saveData(tripId.toString())
            }
            handler.postDelayed(this, 1000)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {

        when (event?.sensor?.type) {

            Sensor.TYPE_LINEAR_ACCELERATION -> {

                accelerationX = event.values[0].toDouble()
                accelerationX_aver.toSum(accelerationX)

                accelerationY = event.values[1].toDouble()
                accelerationY_aver.toSum(accelerationY)

                accelerationZ = event.values[2].toDouble()
                accelerationZ_aver.toSum(accelerationZ)
            }

            Sensor.TYPE_ROTATION_VECTOR -> {
                rotationX = event.values[0].toDouble()
                rotationX_aver.toSum(rotationX)
                rotationY = event.values[1].toDouble()
                rotationY_aver.toSum(rotationY)
                rotationZ = event.values[2].toDouble()
                rotationZ_aver.toSum(rotationZ)
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
            sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
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

        val ref = FirebaseDatabase.getInstance().getReference(userId)

        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (!checkTrip){
                    if(p0!!.exists()) {
                        tripId = p0.children.count()
                        //checkTrip = true
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

    }

    private fun saveData(pathId: String) {

        val refDriver = FirebaseDatabase.getInstance().getReference(userId + "/" + pathId)

        val dataLineId = refDriver.push().key.toString()

        if(calibration.sendDataToDB()){
            //get average values and reset counters
            val accelX = Math.round((accelerationX_aver.getResult() - calibration.accelerationX) * 1000.0)/1000.0
            val accelY = Math.round((accelerationY_aver.getResult() - calibration.accelerationY) * 1000.0)/1000.0
            val accelZ = Math.round((accelerationZ_aver.getResult() - calibration.accelerationZ) * 1000.0)/1000.0
            val rotX = Math.round((rotationX_aver.getResult() - calibration.rotationX) * 1000.0)/1000.0
            val rotY = Math.round((rotationY_aver.getResult() - calibration.rotationY) * 1000.0)/1000.0
            val rotZ = Math.round((rotationZ_aver.getResult() - calibration.rotationZ) * 1000.0)/1000.0

            //update shown values with average
            acceleration = "x = $accelX m/s2\n" +
                    "y = $accelY m/s2\n" +
                    "z = $accelZ m/s2"
            accDta.text = acceleration

            rotation = "x = $rotX rad/s\n" +
                    "y = $rotY rad/s \n" +
                    "z = $rotZ rad/s"
            rotDta.text = rotation

            if(!checkTrip){
                val dataLine = DataLine(calibration.accelerationX, calibration.accelerationY, calibration.accelerationZ,
                    calibration.rotationX, calibration.rotationY, calibration.rotationZ,
                    latitude, longitude,
                    currentDate, currentTime)

                refDriver.child(dataLineId).setValue(dataLine)
                checkTrip = true
            }
            else{
                val dataLine = DataLine(//dataLineId, userId,
                    accelX, accelY, accelZ,
                    rotX, rotY, rotZ,
                    latitude, longitude,
                    //speed,
                    currentDate, currentTime)

                refDriver.child(dataLineId).setValue(dataLine)
            }

        }
    }


}