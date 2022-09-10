package com.example.dataloggerextended.adapters.mainFragment.devices

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dataloggerextended.ui.view.IndividualSensorDataDisplayActivity
import com.example.dataloggerextended.R
import com.example.dataloggerextended.adapters.mainFragment.sensors.SensorsAdapter
import com.example.dataloggerextended.databinding.MainDeviceSectionRvBinding
import com.example.dataloggerextended.model.DeviceData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class DeviceViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = MainDeviceSectionRvBinding.bind(view)
    private lateinit var firebaseAuth: FirebaseAuth
    private val dbUsers = Firebase.firestore.collection("users")
    val recyclerViewDeviceSections = view.findViewById<RecyclerView>(R.id.recyclerViewDeviceSections)

    @SuppressLint("SetTextI18n")
    fun render(deviceModel: List<List<DeviceData?>?>, mainContext: Context) {

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        dbUsers.document(firebaseAuth.currentUser?.email!!)
            .collection("linked_devices")
            .document(deviceModel[0]?.get(0)?.deviceId!!).get().addOnSuccessListener {
                binding.deviceName.text = it.data?.get("devName").toString()
            }

        initRecyclerView(deviceModel, mainContext)
    }

    private fun initRecyclerView(state: List<List<DeviceData?>?>?, mainContext: Context){

        recyclerViewDeviceSections.layoutManager = LinearLayoutManager(mainContext)
        recyclerViewDeviceSections.adapter = SensorsAdapter(state, {onItemSelected(it)})
    }

    private fun onItemSelected(data: List<DeviceData?>){
        val sendData = arrayListOf<String?>()

        sendData.add(data[0]?.deviceId)

        if (data[0]?.hum1 != null){
            sendData.add("hum1")
        }else if(data[0]?.hum2 != null){
            sendData.add("hum2")
        }else if(data[0]?.hum3 != null){
            sendData.add("hum3")
        }else if(data[0]?.temp1 != null){
            sendData.add("temp1")
        }else if(data[0]?.temp2 != null){
            sendData.add("temp2")
        }else if(data[0]?.temp3 != null){
            sendData.add("temp3")
        }else if(data[0]?.switch1 != null){
            sendData.add("switch1")
        }else if(data[0]?.switch2 != null){
            sendData.add("switch2")
        }else if(data[0]?.switch3 != null){
            sendData.add("switch3")
        }

        val intent = Intent(itemView.context, IndividualSensorDataDisplayActivity::class.java)
        intent.putExtra("data", sendData)
        itemView.context.startActivity(intent)
    }
}
