package com.example.dataloggerextended.ui.viewmodel

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dataloggerextended.model.DeviceData
import com.example.dataloggerextended.model.UserDevice
import com.example.dataloggerextended.utils.ScreenState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel() : ViewModel() {

    private val dbUsers = Firebase.firestore.collection("users")
    private val dbDevices = Firebase.firestore.collection("devices")

    private val _devices = MutableLiveData<ScreenState<List<UserDevice?>>>()
    val device: LiveData<ScreenState<List<UserDevice?>>>
        get() = _devices

    private val _devicesWithData = MutableLiveData<ScreenState<List<DeviceData?>>>()
    val deviceWithData: LiveData<ScreenState<List<DeviceData?>>>
        get() = _devicesWithData

    private val _allDevicesWithData = MutableLiveData<ScreenState<List<DeviceData?>>>()
    val allDeviceWithData: LiveData<ScreenState<List<DeviceData?>>>
        get() = _allDevicesWithData

    private val allDataFromDevice = mutableListOf<DeviceData>()

    //Carga los devices que el usuario tenga asociados a su cuenta
    fun getUserDevices(email: String) {

        //activa el loading screen antes de arrancar a buscar los datos
        _devices.postValue(ScreenState.Loading(null))

        //Busco dentro de firebase todos los dispositivos
        dbUsers.document(email).collection("linked_devices").addSnapshotListener { snapshot, e ->

            // si hay una excepcion, se sube el mensaje
            if (e != null) {
                _devices.postValue(ScreenState.Error(e.message.toString(), null))
                return@addSnapshotListener
            }
            // si estamos aca, no hubo errores

            if (snapshot != null) {
                //todos los snapshots (devices) los guardo en una lista
                val allDevices = mutableListOf<UserDevice>()
                val documents = snapshot.documents
                documents.forEach {

                    val device = it.toObject(UserDevice::class.java)
                    if (device != null) {
                        device.device = it.id
                        allDevices.add(device)
                    }
                }
                //Cargo la lista en la variable _devices y la mando con success para continuar
                _devices.postValue(ScreenState.Success(allDevices))

            }
        }
    }

    //Recibe una lista de devices y busca toda su info y la va organizando en diferentes listas
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDataFromDevices() {

        //activa el loading screen antes de arrancar a buscar los datos
        _devicesWithData.postValue(ScreenState.Loading(null))

        //Recorro cada uno de los devices y voy cargando la info obtenida de firebase en una lista
        _devices.value?.data?.forEach { device ->
            device?.device?.let { it ->
                dbDevices.document(it).collection("datos").addSnapshotListener { snapshot, e ->

                    // si hay una excepcion, se sube el mensaje
                    if (e != null) {
                        _devicesWithData.postValue(ScreenState.Error(e.message.toString(), null))
                        return@addSnapshotListener
                    }

                    // si estamos aca, no hubo errores

                    if (snapshot != null) {

                        //convierte cada uno de los documentos de cada device en un DeviceData y los junta en una lista
                        val documents = snapshot.documents

                        documents.forEach { it ->
                            //creo el DeviceData donde se va a ir guardando la info

                            val deviceData = it.toObject(DeviceData::class.java)

                            if (deviceData != null) {
                                //por cada item del documento
                                it.data?.forEach {
                                    //carga los datos de cada titulo del documento dentro del DeviceData
                                    when (it.key) {
                                        "hum1" -> {
                                            deviceData?.hum1 = it.value.toString()
                                        }
                                        "hum2" -> {
                                            deviceData?.hum2 = it.value.toString()
                                        }
                                        "hum3" -> {
                                            deviceData?.hum3 = it.value.toString()
                                        }
                                        "temp1" -> {
                                            deviceData?.temp1 = it.value.toString()
                                        }
                                        "temp2" -> {
                                            deviceData?.temp2 = it.value.toString()
                                        }
                                        "temp3" -> {
                                            deviceData?.temp3 = it.value.toString()
                                        }
                                        "switch1" -> {
                                            deviceData?.switch1 = it.value.toString()
                                        }
                                        "switch2" -> {
                                            deviceData?.switch2 = it.value.toString()
                                        }
                                        "switch3" -> {
                                            deviceData?.switch3 = it.value.toString()
                                        }
                                        "timestamp" -> {
                                            val formatter  = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                                            val timestamp = it.value.toString()
                                            val date = formatter.parse(timestamp)

                                            deviceData?.time = date
                                        }
                                    }
                                }

                                deviceData?.id = it.id
                                deviceData?.deviceId = device.device

                                //Reviso que no hayan datos repetidos
                                val myDevice: DeviceData? =
                                    allDataFromDevice.firstOrNull { it.id == deviceData?.id }

                                if (myDevice == null) {
                                    allDataFromDevice.add(deviceData)
                                } else {
                                    allDataFromDevice.find { it.id == deviceData?.id && it.deviceId == device.device }?.hum1 =
                                        deviceData.hum1
                                    allDataFromDevice.find { it.id == deviceData?.id && it.deviceId == device.device }?.hum2 =
                                        deviceData.hum2
                                    allDataFromDevice.find { it.id == deviceData?.id && it.deviceId == device.device }?.hum3 =
                                        deviceData.hum3
                                    allDataFromDevice.find { it.id == deviceData?.id && it.deviceId == device.device }?.temp1 =
                                        deviceData.temp1
                                    allDataFromDevice.find { it.id == deviceData?.id && it.deviceId == device.device }?.temp2 =
                                        deviceData.temp2
                                    allDataFromDevice.find { it.id == deviceData?.id && it.deviceId == device.device }?.temp3 =
                                        deviceData.temp3
                                    allDataFromDevice.find { it.id == deviceData?.id && it.deviceId == device.device }?.switch1 =
                                        deviceData.switch1
                                    allDataFromDevice.find { it.id == deviceData?.id && it.deviceId == device.device }?.switch2 =
                                        deviceData.switch2
                                    allDataFromDevice.find { it.id == deviceData?.id && it.deviceId == device.device }?.switch3 =
                                        deviceData.switch3
                                }


                            }
                        }
                        _devicesWithData.postValue(ScreenState.Success(allDataFromDevice))
                    }

                }
            }
        }
    }
}