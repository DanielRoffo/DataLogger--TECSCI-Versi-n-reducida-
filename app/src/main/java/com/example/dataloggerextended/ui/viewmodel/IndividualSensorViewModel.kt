package com.example.dataloggerextended.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dataloggerextended.model.SensorData
import com.example.dataloggerextended.utils.ScreenState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class IndividualSensorViewModel() : ViewModel() {

    private val dbDevices = Firebase.firestore.collection("devices")

    private val _individualDataSet = MutableLiveData<ScreenState<List<SensorData?>>>()
    val individualDataSet: LiveData<ScreenState<List<SensorData?>>>
        get() = _individualDataSet

    private var allDataFromSensor = mutableListOf<SensorData?>()

    fun getDataFromSensor(device: String, sensor: String) {

        //activa el loading screen antes de arrancar a buscar los datos
        _individualDataSet.postValue(ScreenState.Loading(null))

        //Recorro cada uno de los devices y voy cargando la info obtenida de firebase en una lista

        dbDevices.document(device).collection("datos").addSnapshotListener { snapshot, e ->

            // si hay una excepcion, se sube el mensaje
            if (e != null) {
                _individualDataSet.postValue(ScreenState.Error(e.message.toString(), null))
                return@addSnapshotListener
            }

            // si estamos aca, no hubo errores

            if (snapshot != null) {

                //convierte cada uno de los documentos de cada device en un DeviceData y los junta en una lista
                val documents = snapshot.documents

                documents.forEach { it ->

                    val sensorData = it.toObject(SensorData::class.java)
                    if (sensorData != null) {
                        it.data?.forEach {
                            //carga los datos de cada titulo del documento dentro del sensorData

                            when (it.key) {
                                sensor -> {
                                    sensorData?.sensorVal = it.value.toString()
                                }
                                "timestamp" -> {

                                    val formatter  = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                                    val timestamp = it.value.toString()
                                    val date = formatter.parse(timestamp)

                                    sensorData?.time = date
                                }
                            }
                        }

                        //Reviso que no hayan datos repetidos
                        val myDevice: SensorData? =
                            allDataFromSensor.firstOrNull { it?.time == sensorData.time }

                        if (myDevice == null) {
                            allDataFromSensor.add(sensorData)
                        }
                    }
                }


                //Cargo la lista en la variable _devices y la mando con success para continuar
                _individualDataSet.postValue(ScreenState.Success(allDataFromSensor))

            }
        }
    }
}