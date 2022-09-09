package com.example.dataloggerextended.ui.view

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dataloggerextended.R

class ConfigurationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)

        // Cambiar el color del SupportActionBar
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.light_black)))
        supportActionBar!!.title = "Notifications"

        //Agrego un boton de regreso al MainActivity y cambio el titulo del action bar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}