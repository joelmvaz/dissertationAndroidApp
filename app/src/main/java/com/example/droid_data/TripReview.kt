package com.example.droid_data

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.trip_review.*

class TripReview : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.trip_review)

        btnExit.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // start next activity
            startActivity(intent)
        }
    }
}