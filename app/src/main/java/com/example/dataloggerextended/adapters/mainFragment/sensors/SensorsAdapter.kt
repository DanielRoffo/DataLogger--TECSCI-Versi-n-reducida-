package com.example.dataloggerextended.adapters.mainFragment.sensors

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dataloggerextended.R
import com.example.dataloggerextended.model.DeviceData

// Le agrego una variable "List" de entrada + le paso el ViewHolder + implemento los atributos del adapter
class SensorsAdapter(private var sensorList: List<List<DeviceData?>?>?, private val onClickListener: (List<DeviceData?>) -> Unit) :
    RecyclerView.Adapter<SensorViewHolder>() {

    // Devuelve un item (en este caso "main_device_card_rv") al ViewHolder por cada objeto que haya en la lista "sensorList"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SensorViewHolder(layoutInflater.inflate(R.layout.main_device_card_rv, parent, false))
    }

    // Este metodo va a pasar por cada uno de los items del listado y va a llamar al Sensor Render pasandole ese item.
    // La fun recibe la instancia del viewHolder y la posiscion en la que estamos.
    override fun onBindViewHolder(holder: SensorViewHolder, position: Int) {
        val item = sensorList?.get(position)

        if (item != null) {
            holder.render(item, onClickListener)
        }
    }

    // Devuelve el largo del listado, si le paso por ej "8" items, el listado solo mostrara 8 de sus items
    // En el caso de q el listado contenga menos de "8" items, la app se crasheara
    override fun getItemCount(): Int = sensorList!!.size

}