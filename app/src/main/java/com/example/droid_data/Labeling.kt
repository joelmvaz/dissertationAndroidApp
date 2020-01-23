package com.example.droid_data

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

    private var maxSpeedCity = 16.7         //60km/h
    private var maxSpeedHighway = 25.0      //90km/h
    private var maxAccelCity = 1.9
    private var maxAccelHighway = 2.5

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
            label = "something"
        //and more labels for different options

        return label
    }

    private fun checkAcceleration(acceleration: Double, zone: Boolean): Int{
        // call checkZone to know if it's city or highway
        // check if this acceleration is ok for this zone
        // return points (i.e. 0 - ok, 1 - higher than normal but not critical, 2 - bad, 3 - crazy idiot...)

        if (zone){
            if (acceleration <= maxAccelCity){
                return 0
            }
            else if (acceleration <= (maxAccelCity + 0.5)){
                return 1
            }
            else if (acceleration <= (maxAccelCity + 1.0)) {
                return 2
            }
        }
        else if (!zone){
            if (acceleration <= maxAccelHighway){
                return 0
            }
            else if (acceleration <= (maxAccelHighway + 0.5)){
                return 1
            }
            else if (acceleration <= (maxAccelHighway + 1.0)) {
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
            else if (speed <= (maxSpeedCity + 2.0)){
                return 1
            }
            else if (speed <= (maxSpeedCity + 5.0)) {
                return 2
            }
        }
        else if (!zone){
            if (speed <= maxSpeedHighway){
                return 0
            }
            else if (speed <= (maxSpeedHighway + 2.0)){
                return 1
            }
            else if (speed <= (maxSpeedHighway + 5.0)) {
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