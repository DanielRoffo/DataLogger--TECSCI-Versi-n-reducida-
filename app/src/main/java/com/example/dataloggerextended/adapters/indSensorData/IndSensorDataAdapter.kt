package com.example.dataloggerextended.adapters.indSensorData

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dataloggerextended.R
import com.example.dataloggerextended.model.SensorData
import com.example.dataloggerextended.utils.MydiffUtilSensorInfo

class IndSensorDataAdapter(private var sensorDataList: List<SensorData?>): RecyclerView.Adapter<IndSensorDataViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndSensorDataViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return IndSensorDataViewHolder(layoutInflater.inflate(R.layout.single_sensor_data_rv, parent, false))
    }

    override fun onBindViewHolder(holder: IndSensorDataViewHolder, position: Int) {
        val item = sensorDataList[position]
        if (item != null) {
            holder.render(item)
        }
    }

    override fun getItemCount(): Int {
        return sensorDataList.size
    }

    fun setData(newDeviceList: List<SensorData?>){

        val diffUtil = MydiffUtilSensorInfo(sensorDataList, newDeviceList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        sensorDataList = newDeviceList
        diffResults.dispatchUpdatesTo(this)
    }

}