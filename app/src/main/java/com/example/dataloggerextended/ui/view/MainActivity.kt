package com.example.dataloggerextended.ui.view

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dataloggerextended.NotificationsActivity
import com.example.dataloggerextended.R
import com.example.dataloggerextended.adapters.mainFragment.devices.DevicesAdapter
import com.example.dataloggerextended.databinding.ActivityMainBinding
import com.example.dataloggerextended.model.DeviceData
import com.example.dataloggerextended.model.UserDevice
import com.example.dataloggerextended.ui.viewmodel.MainViewModel
import com.example.dataloggerextended.utils.ScreenState
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {


    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    //Inicializo el ViewModel
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }
    private val myAdapter by lazy { this?.let {
        DevicesAdapter(emptyList<List<List<DeviceData?>>>(),
            it
        )
    } }
    lateinit var toggle: ActionBarDrawerToggle


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Cambiar el color del SupportActionBar
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.light_black)))

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Cargo los devices que el usuario tenga asociados a él
        viewModel.getUserDevices(firebaseAuth.currentUser!!.email!!)

        initRecyclerView()

        //Observo del MainViewModel si hay cambios en los datos de device
        viewModel.device.observe(this , { state ->
            //si se actualizan los devices del usuario procede a buscar los datos de cada devices
            processDevicesResponse(state)
        })

        //Observo del MainViewModel si hay cambios en los datos de deviceWithData
        viewModel.deviceWithData.observe(this, { state ->
            processDevicesDataResponse(state)
        })


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
                    val intent = Intent(this, MainActivity::class.java)
                    this.startActivity(intent)
                }
                R.id.nav_config -> {
                    val intent = Intent(this, MainActivity::class.java)
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

    //Activa o desactiva las vistas de loading dependiendo del estado de los datos recibidos
    @RequiresApi(Build.VERSION_CODES.O)
    private fun processDevicesResponse(state: ScreenState<List<UserDevice?>>) {
        when (state) {
            is ScreenState.Loading -> {
                //Activo el loading
                binding.viewLoading.visibility = View.VISIBLE
            }
            is ScreenState.Success -> {
                //no cambio la visibility del progress bar a gone por q todavia falta q se ejecute la otra funcion
                viewModel.getDataFromDevices()

            }
            is ScreenState.Error -> {
                binding.viewLoading.visibility = View.GONE
                val view = binding.viewLoading.rootView
                Snackbar.make(view, state.message!!, Snackbar.LENGTH_INDEFINITE).show()
            }
        }
    }

    //Activa o desactiva las vistas de loading dependiendo del estado de los datos recibidos
    private fun processDevicesDataResponse(state: ScreenState<List<DeviceData?>>) {
        when (state) {
            is ScreenState.Loading -> {
                //no toco nada aca por q el progress bar ya esta en visible de la otra funcion

            }
            is ScreenState.Success -> {
                binding.viewLoading.visibility = View.GONE
                //Ordeno la lista que llega con todos los datos de cada device
                val organizedList = reorganizeData(state.data)
                //Inicializo el RecyclerView con toda la info de los devices
                //La info se encuentra ordenada en una lista de devices que contiene una lista por cada sensor
                //Y cada sensor tiene una lista de DeviceData

                myAdapter?.setData(organizedList)

            }
            is ScreenState.Error -> {
                binding.viewLoading.visibility = View.GONE
                val view = binding.viewLoading.rootView
                Snackbar.make(view, state.message!!, Snackbar.LENGTH_INDEFINITE).show()

            }
        }
    }

    private fun reorganizeData(dataList: List<DeviceData?>?): MutableList<List<List<DeviceData>>> {

        val listOfList = mutableListOf<List<DeviceData>>()
        val mutableList = mutableListOf<DeviceData>()
        val deviceList = mutableListOf<List<List<DeviceData>>>()

        if (dataList != null) {
            //Organizo cada documento de cada dispositivo, separo la info de cada DeviceData y la guardo en un List
            for (sensor in dataList) {

                val addSensorHum: DeviceData = DeviceData()
                val addSensorTemp: DeviceData = DeviceData()


                if (sensor?.hum != null) {
                    addSensorHum.deviceId = sensor.deviceId
                    addSensorHum.id = sensor.id
                    addSensorHum.time = sensor.time
                    addSensorHum.hum = sensor.hum
                    addSensorHum.temp = null

                }

                if (sensor?.temp != null) {
                    addSensorTemp.deviceId = sensor.deviceId
                    addSensorTemp.id = sensor.id
                    addSensorTemp.time = sensor.time
                    addSensorTemp.hum = null
                    addSensorTemp.temp = sensor.temp

                }
                mutableList.add(addSensorHum)
                mutableList.add(addSensorTemp)

            }
            //En el listado final quedan todos los DeviceData de cada sensor
            //Con group by junto todos los DeviceData que pertenezcan al mismo dispositivo
            val mapDevices = mutableList.groupBy { it.deviceId }

            //Ahora que tengo todos los DeviceData de cada sensor ordenado por DeviceId,
            //guardo los DeviceData de cada sensor por separado dentro diferentes listas (dentro de una lista "DocumentList" q estara
            // contenida por una lista "SensorList" y cada "SensorList" sera guardada dentro de "DeviceList")

            for (devices in mapDevices.values) {

                val humDocumentList = mutableListOf<DeviceData>()
                val tempDocumentList = mutableListOf<DeviceData>()

                for (documents in devices.indices) {

                    if (devices[documents].hum != null) {

                        humDocumentList.add(devices[documents])
                    }
                    if (devices[documents].temp != null) {
                        tempDocumentList.add(devices[documents])
                    }
                }
                val tempListOfSensors = listOf(humDocumentList, tempDocumentList)
                deviceList.add(tempListOfSensors)
            }
        }
        return deviceList
    }

    //Se inicializa el RecyclerView
    private fun initRecyclerView() {
        val recyclerView = binding.sensorRv
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = myAdapter

    }
}