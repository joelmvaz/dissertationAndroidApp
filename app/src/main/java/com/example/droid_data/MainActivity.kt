package com.example.droid_data

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val foodList= arrayListOf("Chinese", "Hamburguer", "Pizza", "Mc", "Italian")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        decideBtn.setOnClickListener {
            val random= Random()
            val randomFood= random.nextInt(foodList.count())
            selectedfoodtext.text= foodList[randomFood]
        }

        addFoodBtn.setOnClickListener {
            val newFood= addFoodText.text.toString()
            if (!newFood.isBlank()) {
                foodList.add(newFood)
                addFoodText.text.clear()
            }
        }
    }
}