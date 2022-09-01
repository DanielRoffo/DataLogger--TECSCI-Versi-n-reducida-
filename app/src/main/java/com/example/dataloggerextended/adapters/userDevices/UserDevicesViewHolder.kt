package com.example.dataloggerextended.adapters.userDevices

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.dataloggerextended.databinding.UserDeviceRvBinding
import com.example.dataloggerextended.model.UserDevice

class UserDevicesViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val binding = UserDeviceRvBinding.bind(view)

    fun render(userDeviceModel: UserDevice){
        binding.deviceId.text = userDeviceModel.device
    }

}