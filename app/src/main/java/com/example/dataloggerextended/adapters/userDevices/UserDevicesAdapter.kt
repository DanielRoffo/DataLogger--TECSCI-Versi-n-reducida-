package com.example.dataloggerextended.adapters.userDevices

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dataloggerextended.R
import com.example.dataloggerextended.model.UserDevice
import com.example.dataloggerextended.utils.MydiffUtilUserDevices

class UserDevicesAdapter(private var userDeviceList: List<UserDevice>): RecyclerView.Adapter<UserDevicesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserDevicesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return UserDevicesViewHolder(layoutInflater.inflate(R.layout.user_device_rv, parent, false))
    }

    override fun onBindViewHolder(holder: UserDevicesViewHolder, position: Int) {
        val item = userDeviceList[position]
        holder.render(item)
    }

    override fun getItemCount(): Int {
        return userDeviceList.size
    }

    fun setData(data: List<UserDevice>) {
        val diffUtil = MydiffUtilUserDevices(userDeviceList, data)
        val diffResults = DiffUtil.calculateDiff(diffUtil)
        userDeviceList = data
        diffResults.dispatchUpdatesTo(this)
    }
}