package com.example.droid_data

class Labeling{


    /*
                         *LongTop

          *LatLeft   Porto(minLat, minLong)   *LatRight

                         *LongBottom
     */

    private var LatitudePorto = 41.16
    private var LatitudeLeftPorto = 41.111
    private var LatitudeRihtPorto = 41.207
    private var LongitudePorto = -8.56
    private var LongitudeTopPorto = -8.555
    private var LongitudeBottomPorto = -8.649

    private var LatitudeGandra = 41.183
    private var LatitudeLeftGandra = 41.180
    private var LatitudeRihtGandra = 41.185
    private var LongitudeGandra = -8.445
    private var LongitudeTopGandra = -8.448
    private var LongitudeBottomGandra = -8.438

    private var LatitudeValongo = 41.189
    private var LatitudeLeftValongo = 41.183
    private var LatitudeRihtValongo = 41.93
    private var LongitudeValongo = -8.499
    private var LongitudeTopValongo = -8.496
    private var LongitudeBottomValongo = -8.501

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
        // Check If in Porto
        if (latitude > LongitudeBottomPorto && latitude < LongitudeTopPorto) {
            if (longitude > LatitudeRihtPorto && longitude < LatitudeLeftPorto) {
                return 10 // 10 means inside Porto
            }
        }
        //Check If in Gandra
        if (latitude > LongitudeBottomGandra && latitude < LongitudeTopGandra) {
            if (longitude > LatitudeRihtGandra && longitude < LatitudeLeftGandra) {
                return 20 // 20 means inside Gandra
            }
        }

        //Check If in Valongo
        if (latitude > LongitudeBottomValongo && latitude < LongitudeTopValongo) {
            if (longitude > LatitudeRihtValongo && longitude < LatitudeLeftValongo) {
                return 30 // 30 means inside Valongo
            }
        }

        //Check that speed looks high enough > 60km.h
        return 40 // 40 means outside Porto && outside Gandra

        //else
        return 0 // Impossible to know location
    }
}