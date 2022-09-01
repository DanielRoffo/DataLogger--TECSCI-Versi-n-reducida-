package com.example.dataloggerextended.ui.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dataloggerextended.adapters.mainFragment.devices.DevicesAdapter
import com.example.dataloggerextended.databinding.FragmentMainBinding
import com.example.dataloggerextended.model.DeviceData
import com.example.dataloggerextended.model.UserDevice
import com.example.dataloggerextended.utils.ScreenState
import com.example.dataloggerextended.ui.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class MainFragment : Fragment() {

    //Inicializo el ViewModel
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: FragmentMainBinding
    private val myAdapter by lazy { context?.let {
        DevicesAdapter(emptyList<List<List<DeviceData?>>>(),
            it
        )
    } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //ViewBinding
        binding = FragmentMainBinding.inflate(layoutInflater)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        //Cargo los devices que el usuario tenga asociados a él
        viewModel.getUserDevices(firebaseAuth.currentUser!!.email!!)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        initRecyclerView()

        //Observo del MainViewModel si hay cambios en los datos de device
        viewModel.device.observe(viewLifecycleOwner, { state ->
            //si se actualizan los devices del usuario procede a buscar los datos de cada devices
            processDevicesResponse(state)
        })

        //Observo del MainViewModel si hay cambios en los datos de deviceWithData
        viewModel.deviceWithData.observe(viewLifecycleOwner, { state ->
            processDevicesDataResponse(state)
        })


        return binding.root
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
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = myAdapter

    }

}