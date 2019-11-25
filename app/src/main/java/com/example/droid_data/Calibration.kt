package com.example.droid_data

class Calibration (var accelX : CalculateAverage, var accelY : CalculateAverage, var accelZ : CalculateAverage,
                   var rotX : CalculateAverage, var rotY : CalculateAverage, var rotZ : CalculateAverage) {
    public var accelerationX = 0.0
    public var accelerationY = 0.0
    public var accelerationZ = 0.0
    public var rotationX = 0.0
    public var rotationY = 0.0
    public var rotationZ = 0.0
    private var timeLeft = 3

    public fun sendDataToDB(): Boolean{
        if (timeLeft > 1){
            timeLeft--
            return false
        }
        else if(timeLeft == 1){
            accelerationX = accelX.getResult()
            accelerationY = accelY.getResult()
            accelerationZ = accelZ.getResult()
            rotationX = rotX.getResult()
            rotationY = rotY.getResult()
            rotationZ = rotZ.getResult()
            timeLeft--
            return false
        }
        else
            return true
    }

}