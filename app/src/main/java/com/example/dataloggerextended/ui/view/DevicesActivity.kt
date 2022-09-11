package com.example.dataloggerextended.ui.view

import android.content.ContentValues
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dataloggerextended.R
import com.example.dataloggerextended.adapters.userDevices.UserDevicesAdapter
import com.example.dataloggerextended.databinding.ActivityDevicesBinding
import com.example.dataloggerextended.model.UserDevice
import com.example.dataloggerextended.ui.viewmodel.MainViewModel
import com.example.dataloggerextended.utils.ScreenState
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DevicesActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityDevicesBinding
    private val dbDevices = Firebase.firestore.collection("devices")
    private val dbDevicesUsers = Firebase.firestore.collection("users")
    private val myAdapter by lazy { UserDevicesAdapter(emptyList<UserDevice>()) }
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devices)

        // Cambiar el color del SupportActionBar
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.light_black)))
        supportActionBar!!.title = "Your Devices"

        //Agrego un boton de regreso al MainActivity y cambio el titulo del action bar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding = ActivityDevicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        lifecycleScope.launch(Dispatchers.IO){
            viewModel.getUserDevices(firebaseAuth.currentUser!!.email!!)
        }

        initRecyclerView()

        //Observo del MainViewModel si hay cambios en los datos de movieLiveData
        viewModel.device.observe(this ,{ state ->
            lifecycleScope.launch(Dispatchers.IO) {
                processDevicesResponse(state)
            }
        })

        addNewDevice()


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

    private fun processDevicesResponse(state: ScreenState<List<UserDevice?>>) {
        when (state) {
            is ScreenState.Loading -> {

            }
            is ScreenState.Success -> {

                myAdapter.setData(state.data as List<UserDevice>)

            }
            is ScreenState.Error ->{

            }
        }
    }

    //Se inicializa el RecyclerView
    private fun initRecyclerView() {
        val layoutManager = GridLayoutManager(this, 3)
        val recyclerView = binding.devicesIdRv
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = myAdapter

    }

    private fun addNewDevice(){
        binding.addNewDevice.setOnClickListener {

            // Creacion de un alertDialog --> Ventana q se abre dentro de un mismo activity.
            // Se pasan los valores
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.set_code_and_model_alert_dialog, null)

            // Se pasa la vista al builder
            builder.setView(view)
            //Se crea el Dialog
            val dialog = builder.create()
            dialog.show()

            dialog.findViewById<AppCompatButton>(R.id.btnAcceptDialog)?.setOnClickListener {

                val devId = dialog.findViewById<TextInputEditText>(R.id.etDeviceId)!!.text.toString()
                val devCode = dialog.findViewById<TextInputEditText>(R.id.etDeviceCode)!!.text.toString()

                if (devId.isNotEmpty() && devCode.isNotEmpty()) {

                    dbDevicesUsers.document(firebaseAuth.currentUser!!.email!!).collection("linked_devices").document(devId)
                        .get().addOnSuccessListener {
                            if (it.data.isNullOrEmpty()){
                                dbDevices.document(devId).get()
                                    .addOnSuccessListener { document ->
                                        if (document.data == null){
                                            Toast.makeText(this, "Couldn't find device = $devId", Toast.LENGTH_SHORT).show()
                                        }else{
                                            if (document.data!!.containsValue(devCode)){
                                                Toast.makeText(this, "Set up your new device!", Toast.LENGTH_SHORT).show()
                                                val intent = Intent(this, AddDeviceActivity::class.java)
                                                val extras = Bundle()
                                                extras.putString("device", devId)
                                                extras.putString("model", document.data!!["model"].toString())
                                                intent.putExtra("extras", extras)
                                                startActivity(intent)
                                            }else if (!document.data!!.containsValue(devCode)){
                                                Toast.makeText(this, "Wrong code. Try Again!", Toast.LENGTH_SHORT).show()
                                            }else{
                                                Toast.makeText(this, "Wrong Id. Try Again!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Ups, something went wrong. Try again!", Toast.LENGTH_SHORT).show()
                                    }

                            }else{
                                Toast.makeText(this, "You already have this device!", Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error Reading document", e) }
                }else {
                    Toast.makeText(this, "Ups, something is missing. Complete all the fields and try again!", Toast.LENGTH_SHORT).show()
                }

            }

            dialog.findViewById<AppCompatButton>(R.id.btnCancelDialog)?.setOnClickListener{ dialog.dismiss() }

            }

        binding.scanNewDevice.setOnClickListener {
            val intent = Intent(this, ScannerAddDeviceActivity::class.java)
            startActivity(intent)
        }

    }
}