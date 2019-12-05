package com.example.droid_data

class Labeling{
    private var minLatitude = 41.17
    private var maxLatitude = 41.18
    private var minLongitude = -8.56
    private var maxLongitude = -8.55
    private var maxSpeedCity = 16.7         //60km/h
    private var maxSpeedHighway = 25.0      //90km/h
    private var maxAccelCity = 1.2
    private var maxAccelHighway = 2.0

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

    private fun checkAcceleration(acceleration: Double, zone: Int): Int{

        // call checkZone to know if it's city or highway
        // check if this acceleration is ok for this zone
        // return points (i.e. 0 - ok, 1 - higher than normal but not critical, 2 - bad, 3 - crazy idiot...)

        if (zone == 0){
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
        else if (zone == 1){
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

    private fun checkSpeed(speed: Double, zone: Int): Int{

        // call checkZone to know if it's city or highway
        // check if this speed is ok for this zone
        // return points (i.e. 0 - ok, 1 - higher than normal but not critical, 2 - bad, 3 - crazy idiot...)

        if (zone == 0){
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
        else if (zone == 1){
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

    private fun checkZone(latitude: Double, longitude: Double): Int{

        // return 0 if city, 1 if highway etc...

        if (latitude > minLatitude && latitude < maxLatitude) {
            if (longitude > minLongitude && longitude < maxLongitude) {
                return 0
            }
        }
        return 1
    }
}