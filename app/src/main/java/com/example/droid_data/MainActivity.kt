package com.example.droid_data

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var uniqueUserId= 1
    private var acceleration= ""
    private var rotation= ""
    private var gravity= ""
    private var magnet= ""



    override fun onSensorChanged(event: SensorEvent?) {

        when (event?.sensor?.type) {

            Sensor.TYPE_ACCELEROMETER -> {
                acceleration = "x = ${event.values[0]} m/s2\n" +
                        "y = ${event.values[1]} m/s2\n" +
                        "z = ${event.values[2]} m/s2"
                accDta.text = acceleration
            }

            Sensor.TYPE_GYROSCOPE -> {
                rotation = "x = ${event.values[0]} rad/s\n" +
                        "y = ${event.values[1]} rad/s \n" +
                        "z = ${event.values[2]} rad/s"
                rotDta.text = rotation
            }
            Sensor.TYPE_GRAVITY -> {
                gravity = "y = ${event.values[1]} m/s2\n"
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
    }
}