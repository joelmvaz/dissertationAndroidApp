package com.example.droid_data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    lateinit var sensorManager: SensorManager
    private var uniqueUserId= 1
    private var acceleration= ""

    override fun onSensorChanged(event: SensorEvent?) {
        acceleration= "   x= ${event?.values?.get(0)} m/s2\n" +
                "   y= ${event?.values?.get(1)} m/s2\n" +
                "   z= ${event?.values?.get(2)} m/s2"

        accDta.text= acceleration
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager= getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )

        //Sets the user id
        usrId.text= uniqueUserId.toString()
    }
}