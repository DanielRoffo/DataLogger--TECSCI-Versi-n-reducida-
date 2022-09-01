package com.example.dataloggerextended.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.dataloggerextended.model.SensorData

class MydiffUtilSensorInfo (
    private val oldList: List<SensorData?>,
    private val newList: List<SensorData?>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]== newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when{
            oldList[oldItemPosition]?.sensorVal == newList[newItemPosition]?.sensorVal -> {
                false
            }
            oldList[oldItemPosition]?.time == newList[newItemPosition]?.time -> {
                false
            }
            else -> true
        }
    }
}




