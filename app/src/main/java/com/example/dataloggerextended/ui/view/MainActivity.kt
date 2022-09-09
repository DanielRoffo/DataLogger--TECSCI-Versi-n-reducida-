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
    @RequiresApi(Build.VERSION_CODES.N)
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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun reorganizeData(dataList: List<DeviceData?>?): MutableList<List<List<DeviceData>>> {

        val listOfList = mutableListOf<List<DeviceData>>()
        val mutableList = mutableListOf<DeviceData>()
        val deviceList = mutableListOf<List<List<DeviceData>>>()

        if (dataList != null) {
            //Organizo cada documento de cada dispositivo, separo la info de cada DeviceData y la guardo en un List
            for (sensor in dataList) {


                val addSensorHum1: DeviceData = DeviceData()
                val addSensorHum2: DeviceData = DeviceData()
                val addSensorHum3: DeviceData = DeviceData()
                val addSensorTemp1: DeviceData = DeviceData()
                val addSensorTemp2: DeviceData = DeviceData()
                val addSensorTemp3: DeviceData = DeviceData()
                val addSensorSwitch1: DeviceData = DeviceData()
                val addSensorSwitch2: DeviceData = DeviceData()
                val addSensorSwitch3: DeviceData = DeviceData()


                if (sensor?.hum1 != null) {
                    addSensorHum1.deviceId = sensor.deviceId
                    addSensorHum1.id = sensor.id
                    addSensorHum1.time = sensor.time
                    addSensorHum1.hum1 = sensor.hum1

                }

                if (sensor?.hum2 != null) {
                    addSensorHum2.deviceId = sensor.deviceId
                    addSensorHum2.id = sensor.id
                    addSensorHum2.time = sensor.time
                    addSensorHum2.hum2 = sensor.hum2

                }

                if (sensor?.hum3 != null) {
                    addSensorHum3.deviceId = sensor.deviceId
                    addSensorHum3.id = sensor.id
                    addSensorHum3.time = sensor.time
                    addSensorHum3.hum3 = sensor.hum3

                }

                if (sensor?.temp1 != null) {
                    addSensorTemp1.deviceId = sensor.deviceId
                    addSensorTemp1.id = sensor.id
                    addSensorTemp1.time = sensor.time
                    addSensorTemp1.temp1 = sensor.temp1
                }

                if (sensor?.temp2 != null) {
                    addSensorTemp2.deviceId = sensor.deviceId
                    addSensorTemp2.id = sensor.id
                    addSensorTemp2.time = sensor.time
                    addSensorTemp2.temp2 = sensor.temp2
                }

                if (sensor?.temp3 != null) {
                    addSensorTemp3.deviceId = sensor.deviceId
                    addSensorTemp3.id = sensor.id
                    addSensorTemp3.time = sensor.time
                    addSensorTemp3.temp3 = sensor.temp3
                }

                if (sensor?.switch1 != null) {
                    addSensorSwitch1.deviceId = sensor.deviceId
                    addSensorSwitch1.id = sensor.id
                    addSensorSwitch1.time = sensor.time
                    addSensorSwitch1.switch1 = sensor.switch1
                }

                if (sensor?.switch2 != null) {
                    addSensorSwitch2.deviceId = sensor.deviceId
                    addSensorSwitch2.id = sensor.id
                    addSensorSwitch2.time = sensor.time
                    addSensorSwitch2.switch2 = sensor.switch2
                }

                if (sensor?.switch3 != null) {
                    addSensorSwitch3.deviceId = sensor.deviceId
                    addSensorSwitch3.id = sensor.id
                    addSensorSwitch3.time = sensor.time
                    addSensorSwitch3.switch3 = sensor.switch3
                }

                mutableList.add(addSensorHum1)
                mutableList.add(addSensorHum2)
                mutableList.add(addSensorHum3)
                mutableList.add(addSensorTemp1)
                mutableList.add(addSensorTemp2)
                mutableList.add(addSensorTemp3)
                mutableList.add(addSensorSwitch1)
                mutableList.add(addSensorSwitch2)
                mutableList.add(addSensorSwitch3)

            }
            //En el listado final quedan todos los DeviceData de cada sensor
            //Con group by junto todos los DeviceData que pertenezcan al mismo dispositivo
            val mapDevices = mutableList.groupBy { it.deviceId }

            //Ahora que tengo todos los DeviceData de cada sensor ordenado por DeviceId,
            //guardo los DeviceData de cada sensor por separado dentro diferentes listas (dentro de una lista "DocumentList" q estara
            // contenida por una lista "SensorList" y cada "SensorList" sera guardada dentro de "DeviceList")

            for (devices in mapDevices.values) {

                val humDocumentList1 = mutableListOf<DeviceData>()
                val humDocumentList2 = mutableListOf<DeviceData>()
                val humDocumentList3 = mutableListOf<DeviceData>()
                val tempDocumentList1 = mutableListOf<DeviceData>()
                val tempDocumentList2 = mutableListOf<DeviceData>()
                val tempDocumentList3 = mutableListOf<DeviceData>()
                val switchDocumentList1 = mutableListOf<DeviceData>()
                val switchDocumentList2 = mutableListOf<DeviceData>()
                val switchDocumentList3 = mutableListOf<DeviceData>()

                for (documents in devices.indices) {

                    if (devices[documents].hum1 != null) {

                        humDocumentList1.add(devices[documents])
                    }
                    if (devices[documents].hum2 != null) {

                        humDocumentList2.add(devices[documents])
                    }
                    if (devices[documents].hum3 != null) {

                        humDocumentList3.add(devices[documents])
                    }
                    if (devices[documents].temp1 != null) {
                        tempDocumentList1.add(devices[documents])
                    }
                    if (devices[documents].temp2 != null) {
                        tempDocumentList2.add(devices[documents])
                    }
                    if (devices[documents].temp3 != null) {
                        tempDocumentList3.add(devices[documents])
                    }
                    if (devices[documents].switch1 != null) {
                        switchDocumentList1.add(devices[documents])
                    }
                    if (devices[documents].switch2 != null) {
                        switchDocumentList2.add(devices[documents])
                    }
                    if (devices[documents].switch3 != null) {
                        switchDocumentList3.add(devices[documents])
                    }
                }
                val tempListOfSensors = mutableListOf(humDocumentList1, humDocumentList2, humDocumentList3, tempDocumentList1, tempDocumentList2,
                tempDocumentList3, switchDocumentList1, switchDocumentList2, switchDocumentList3)
                tempListOfSensors.removeIf{it == emptyList<DeviceData>()}

                if (!tempListOfSensors.isEmpty()){
                    deviceList.add(tempListOfSensors)
                }

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