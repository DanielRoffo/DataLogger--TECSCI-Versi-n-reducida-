package com.example.dataloggerextended.adapters.mainFragment.devices

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dataloggerextended.R
import com.example.dataloggerextended.model.DeviceData
import com.example.dataloggerextended.utils.MydiffUtil

class DevicesAdapter(private var deviceList: List<List<List<DeviceData?>?>?>, private val mainContext: Context): RecyclerView.Adapter<DeviceViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return DeviceViewHolder(layoutInflater.inflate(R.layout.main_device_section_rv, parent, false))
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val item = deviceList[position]
        if (item != null) {
            holder.render(item, mainContext)
        }

    }

    override fun getItemCount(): Int = deviceList.size

    fun setData(newDeviceList: List<List<List<DeviceData?>?>?>){

        val diffUtil = MydiffUtil(deviceList, newDeviceList)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        deviceList = newDeviceList
        diffResults.dispatchUpdatesTo(this)
    }
}