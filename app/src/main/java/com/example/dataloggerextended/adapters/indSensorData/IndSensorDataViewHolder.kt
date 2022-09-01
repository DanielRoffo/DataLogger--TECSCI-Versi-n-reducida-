package com.example.dataloggerextended.adapters.indSensorData

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.dataloggerextended.databinding.SingleSensorDataRvBinding
import com.example.dataloggerextended.model.SensorData
import java.text.SimpleDateFormat

class IndSensorDataViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val dateFormated = SimpleDateFormat("dd/MM/yy || HH:mm:ss")
    val binding = SingleSensorDataRvBinding.bind(view)

    fun render(deviceModel: SensorData){

        binding.value.text = deviceModel.sensorVal
        binding.time.text = dateFormated.format(deviceModel.time)
        binding.alarm.text = "---"
    }
}