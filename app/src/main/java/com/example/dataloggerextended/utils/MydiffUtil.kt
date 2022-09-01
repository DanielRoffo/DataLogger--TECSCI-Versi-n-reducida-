package com.example.dataloggerextended.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.dataloggerextended.model.DeviceData

class MydiffUtil(
    private val oldList: List<List<List<DeviceData?>?>?>,
    private val newList: List<List<List<DeviceData?>?>?>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]?.forEach { it?.forEach { it?.id } } == newList[newItemPosition]?.forEach { it?.forEach { it?.id } }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when{
            oldList[oldItemPosition]?.forEach { it?.forEach { it?.hum } } == newList[newItemPosition]?.forEach { it?.forEach { it?.hum } } -> {
                false
            }
            oldList[oldItemPosition]?.forEach { it?.forEach { it?.temp } } == newList[newItemPosition]?.forEach { it?.forEach { it?.temp } } -> {
                false
            }
            oldList[oldItemPosition]?.forEach { it?.forEach { it?.time } } == newList[newItemPosition]?.forEach { it?.forEach { it?.time } } -> {
                false
            }
            else -> true
        }
    }
}