package com.example.droid_data

class CalculateAverage {
    private var sum = 0.0
    private var n = 0
    private var result = 0.0

    public fun getResult(): Double{
        result = sum / n
        sum = 0.0
        n = 0
        return result
    }

    public fun toSum (x: Double){
        sum += x
        n++
    }
}