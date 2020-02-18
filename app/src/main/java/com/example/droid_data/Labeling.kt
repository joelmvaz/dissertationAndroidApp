package com.example.droid_data

import kotlin.math.abs

class Labeling{

    private val cities = listOf(
        //TODO add more cities to list or implement other logic
        City("Porto", 41.16, -8.555,
            41.111, 41.207,
            -8.555, -8.649),
        City("Gandra", 41.183, -8.445,
            41.180, 41.185,
            -8.438, -8.448),
        City("Valongo", 41.189, -8.499,
            41.183, 41.93,
            -8.496, -8.501)
        )

    private var maxSpeedCity = 15  //50km.h //16.7         //60km/h
    private var maxSpeedHighway = 25.0      //90km/h
    private var maxAccelCity = 1.75 //https://fdotwww.blob.core.windows.net/sitefinity/docs/default-source/content/rail/publications/studies/safety/accelerationresearch.pdf?sfvrsn=716a4bb1_0
    private var maxAccelHighway = 1.85 //https://fdotwww.blob.core.windows.net/sitefinity/docs/default-source/content/rail/publications/studies/safety/accelerationresearch.pdf?sfvrsn=716a4bb1_0

    public fun getLabel(acceleration: Double, speed: Double, latitude: Double, longitude: Double): String{

        var label = ""
        var zone = checkZone(latitude, longitude)
        var accelPoints = checkAcceleration(acceleration, zone)
        var speedPoints = checkSpeed(speed, zone)

        var sum = accelPoints + speedPoints

        if (sum == 0)
            label = "good"
        else if (sum == 1)
            label = "ok"
        else if (sum == 2)
            label = "dangerous"
        else if (sum == 3)
            label = "criminal"
        //and more labels for different options

        return label
    }

    private fun checkAcceleration(acceleration: Double, zone: Boolean): Int{
        // call checkZone to know if it's city or highway
        // check if this acceleration is ok for this zone
        // return points (i.e. 0 - ok, 1 - higher than normal but not critical, 2 - bad, 3 - crazy idiot...)

        if (zone){
            if ( abs(acceleration) <= maxAccelCity){
                return 0
            }
            else if ( abs(acceleration) <= (maxAccelCity + 0.25)){
                return 1
            }
            else if ( abs(acceleration) <= (maxAccelCity + 0.75)) {
                return 2
            }
        }
        else if (!zone){
            if ( abs(acceleration) <= maxAccelHighway){
                return 0
            }
            else if ( abs(acceleration) <= (maxAccelHighway + 0.5)){
                return 1
            }
            else if ( abs(acceleration) <= (maxAccelHighway + 1.0)) {
                return 2
            }
        }
        return 3
    }

    private fun checkSpeed(speed: Double, zone: Boolean): Int{

        // call checkZone to know if it's city or highway
        // check if this speed is ok for this zone
        // return points (i.e. 0 - ok, 1 - higher than normal but not critical, 2 - bad, 3 - crazy idiot...)

        if (zone){
            if (speed <= maxSpeedCity){
                return 0
            }
            else if (speed <= (maxSpeedCity + 7.5)){
                return 1
            }
            else if (speed <= (maxSpeedCity + 10.0)) {
                return 2
            }
        }
        else if (!zone){
            if (speed <= maxSpeedHighway){
                return 0
            }
            else if (speed <= (maxSpeedHighway + 15.0)){
                return 1
            }
            else if (speed <= (maxSpeedHighway + 25.0)) {
                return 2
            }
        }
        return 3
    }

    private fun checkZone(latitude: Double, longitude: Double): Boolean{

        for (city in cities){
            if (city.inCity(latitude, longitude)){
                return true
            }
        }
        return false
    }
}