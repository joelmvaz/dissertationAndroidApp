package com.example.droid_data

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.trip_review.*

class TripReview : Activity() {
    private var labelGood=0
    private var labelOk=0
    private var labelDangerous=0
    private var labelCriminal=0
    private var labelTotal=0
    private var numStars=0.0

    private var tripRev= ""

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.trip_review)

        labelGood= intent.getIntExtra("labelGood", 0)
        labelOk= intent.getIntExtra("labelOk", 0)
        labelDangerous= intent.getIntExtra("labelDangerous", 0)
        labelCriminal= intent.getIntExtra("labelCriminal", 0)
        labelTotal= intent.getIntExtra("labelTotal", 0)

        if(labelTotal <= 1800){ //30 min trip
            if (labelCriminal < 3 && labelDangerous < 25){
                tripRev="Good Behavior, the trip went fine"
                numStars=5.0
            }
            if (labelCriminal < 3 && labelDangerous in 25..50){
                tripRev="Moderate to dangerous Behavior Detected, some over speed detected"
                numStars=3.0
            }

            if (labelCriminal < 3 && labelDangerous in 51..100){
                tripRev="Dangerous Behavior Detected, major over speed and acceleration"
                numStars=2.0
            }

            if (labelCriminal >= 3 || labelDangerous >= 101){
                tripRev="Very Dangerous Behavior Detected, criminal behavior detected in this trip, major over speed"
                numStars=1.0
            }
        }
        if(labelTotal in 1801..3600){ //30 to 1 hour trip
            if (labelCriminal < 3 && labelDangerous < 50){
                tripRev="Good Behavior, the trip went fine"
                numStars=5.0
            }
            if (labelCriminal < 3 && labelDangerous in 50..100){
                tripRev="Moderate to dangerous Behavior Detected, some over speed detected"
                numStars=3.0
            }

            if (labelCriminal < 3 && labelDangerous in 101..200){
                tripRev="Dangerous Behavior Detected, major over speed and acceleration"
                numStars=2.0
            }
            if (labelCriminal >= 3 || labelDangerous >= 201){
                tripRev="Very Dangerous Behavior Detected"
                numStars=1.0
            }
        }
        if(labelTotal in 3601..7200){ //1 to 2 hour trip
            if (labelCriminal < 3 && labelDangerous < 100){
                tripRev="Good Behavior, the trip went fine"
                numStars=5.0
            }
            if (labelCriminal < 3 && labelDangerous in 100..200){
                tripRev="Moderate to dangerous Behavior Detected, some over speed detected"
                numStars=3.0
            }

            if (labelCriminal < 3 && labelDangerous in 201..300){
                tripRev="Dangerous Behavior Detected, major over speed and acceleration"
                numStars=2.0
            }
            if (labelCriminal >= 3 || labelDangerous >= 301){
                tripRev="Very Dangerous Behavior Detected"
                numStars=1.0
            }
        }
        if(labelTotal > 7200){ // greater than 2 hour trip
            if (labelCriminal < 3 && labelDangerous < 200){
                tripRev="Good Behavior, the trip went fine"
                numStars=5.0
            }
            if (labelCriminal < 3 && labelDangerous in 200..300){
                tripRev="Moderate to dangerous Behavior Detected, some over speed detected"
                numStars=3.0
            }

            if (labelCriminal < 3 && labelDangerous in 301..400){
                tripRev="Dangerous Behavior Detected, major over speed and acceleration"
                numStars=2.0
            }
            if (labelCriminal >= 3 || labelDangerous >= 401){
                tripRev="Very Dangerous Behavior Detected"
                numStars=1.0
            }
        }

        durationView.text= labelTotal.toString() + " Seconds"
        classificationView.text= tripRev
        moderateView.text= labelDangerous.toString() + " Times"
        extremeView.text= labelCriminal.toString() + " Times"

        btnExit.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // start next activity
            startActivity(intent)
        }
    }
}