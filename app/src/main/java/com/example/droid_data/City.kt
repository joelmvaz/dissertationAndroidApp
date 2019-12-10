package com.example.droid_data

class City(val name: String, val latitudeCentral: Double, val longitudeCentral: Double,
           val latitudeLeft: Double, val latitudeRight: Double,
           val longitudeTop: Double, val longitudeBottom: Double) {
    public var CityName = name
    private var latCentral = latitudeCentral
    private var longCentral = longitudeCentral
    private var longTop = longitudeTop
    private var longBottom = longitudeBottom
    private var latLeft = latitudeLeft
    private var latRight = latitudeRight

    public fun inCity(longitude: Double, latitude: Double): Boolean {
        if (longitude > longBottom) {
            if (longitude < longTop) {
                if (latitude > latLeft) {
                    if (latitude < latRight){
                        return true
                    }
                }
            }
        }
        return false
    }
}