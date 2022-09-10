package com.example.dataloggerextended.ui.view

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.dataloggerextended.R
import com.example.dataloggerextended.databinding.ActivityConfigurationBinding
import com.example.dataloggerextended.databinding.ActivityNotificationsBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class NotificationsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationsBinding
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Cambiar el color del SupportActionBar
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.light_black)))
        supportActionBar!!.title = "Notifications"

        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    this.startActivity(intent)
                }
                R.id.nav_devices -> {
                    val intent = Intent(this, DevicesActivity::class.java)
                    this.startActivity(intent)
                }
                R.id.nav_notifications -> {
                    val intent = Intent(this, NotificationsActivity::class.java)
                    this.startActivity(intent)
                }
                R.id.nav_shop -> {
                    val intent = Intent(this, ShopActivity::class.java)
                    this.startActivity(intent)
                }
                R.id.nav_config -> {
                    val intent = Intent(this, ConfigurationActivity::class.java)
                    this.startActivity(intent)
                }
                R.id.nav_logout -> {
                    firebaseAuth.signOut()
                    val intent = Intent(this, SignInActivity::class.java)
                    this.startActivity(intent)
                }
                R.id.nav_share -> Toast.makeText(
                    applicationContext,
                    "Clicked Share",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.nav_rate_us -> Toast.makeText(
                    applicationContext,
                    "Clicked Rate Us",
                    Toast.LENGTH_SHORT
                ).show()

            }

            true
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}