package com.example.dataloggerextended.adapters.mainFragment.sensors

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.dataloggerextended.R
import com.example.dataloggerextended.databinding.MainDeviceCardRvBinding
import com.example.dataloggerextended.model.DeviceData
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// El ViewHolder sera llamado con cada uno de los items del listado
class SensorViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val binding = MainDeviceCardRvBinding.bind(view)

    @SuppressLint("SetTextI18n", "SimpleDateFormat", "ResourceAsColor")
    fun render(sensorModel: List<DeviceData?>, onClickListener: (List<DeviceData?>) -> Unit) {



        // Ordeno los listados de los sensores por Dia y Hora
        var listanueva = sensorModel.sortedBy { it?.time }

        setValuesOnRecyclerView(listanueva)


        binding.cardInfo.setOnClickListener {
            onClickListener(sensorModel)
        }
    }

    //Inicializo el Linear Chart
    private fun setLineChartData(entry: List<Entry>, isStepped: Boolean) {


        val linedataset = LineDataSet(entry, "value")

        styleLineDataSet(linedataset, isStepped)


        val finaldataset = ArrayList<LineDataSet>()


        finaldataset.add(linedataset)
        val data = LineData(finaldataset as List<ILineDataSet>)
        binding.lineChart.data = data

        binding.lineChart.xAxis.valueFormatter = XAxisTimeFormatterTwo()

        styleChart(binding.lineChart)

    }

    //Configuro el Chart
    private fun styleChart(lineChart: LineChart) = lineChart.apply {

        setTouchEnabled(true)
        isDragEnabled = true
        setScaleEnabled(false)
        setPinchZoom(false)

        setDescription(null)
        legend.isEnabled = false
        setNoDataText("Sensor not initialize")
        setDrawGridBackground(false)
        setDrawBorders(false)
        setMaxVisibleValueCount(10)

        setBackgroundColor(binding.root.resources.getColor(R.color.lighter_black))
        xAxis.setTextColor(binding.root.resources.getColor(R.color.gold))
        xAxis.setTextSize(12f)
        xAxis.labelRotationAngle = -40.0F
        axisLeft.setTextColor(binding.root.resources.getColor(R.color.gold))
        axisLeft.setTextSize(12f)
        axisRight.setTextColor(binding.root.resources.getColor(R.color.gold))
        axisRight.setTextSize(12f)
        legend.setTextColor(binding.root.resources.getColor(R.color.gold))
        legend.setTextSize(12f)

        axisRight.isEnabled = false

        axisLeft.isEnabled = false
        axisLeft.mAxisMinimum = 0f
        axisLeft.mAxisMaximum = 10f

        xAxis.mAxisMinimum = 0f
        xAxis.mAxisMaximum = 24f
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.position = XAxis.XAxisPosition.TOP

    }

    //Configuro la linea del Chart
    private fun styleLineDataSet(lineDataSet: LineDataSet, isStepped: Boolean) = lineDataSet.apply {
        color = binding.root.resources.getColor(R.color.gold)
        valueTextColor = binding.root.resources.getColor(R.color.gold)
        setDrawValues(false)

        lineWidth = 4f
        isHighlightEnabled = true
        setDrawHighlightIndicators(false)
        setDrawCircles(true)

        if (isStepped){
            mode = LineDataSet.Mode.STEPPED
        }else{
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        setDrawFilled(true)
        fillDrawable = binding.root.resources.getDrawable(R.drawable.bg_shadow_graph_line_gold)

    }

    @SuppressLint("ResourceAsColor")
    fun setValuesOnRecyclerView(listanueva : List<DeviceData?>){

        val dateFormatedtwo = SimpleDateFormat("dd/MM/yy || HH:mm:ss")

        // Reviso si el listado es un listado de Humedad
        if (listanueva[0]?.hum1 != null) {

            var hum1Entry = ArrayList<Entry>()
            var lastValue = listanueva.size - 1

            //Cargo los datos en de los ultimos valores en la tarjeta
            binding.LastSensorValue.text = "${listanueva[lastValue]?.hum1?.toFloat()} %"
            binding.sensorType.text = "Humidity"
            binding.lastTimeActualization.text = "Act. ${dateFormatedtwo.format(listanueva[lastValue]?.time)} hs"

            //Cargo los datos dentro de un array en orden y con su indice
            listanueva.forEach { it ->
                //Formateo el tiempo
                val timestamp = it?.time!!.time
                val index = timestamp.toFloat()

                hum1Entry.add(Entry(index, it?.hum1.toString().toFloat()))

            }

            //Inicializo el Linear Chart
            setLineChartData(hum1Entry, false)
        }

        if (listanueva[0]?.hum2 != null) {

            var hum1Entry = ArrayList<Entry>()
            var lastValue = listanueva.size - 1

            //Cargo los datos en de los ultimos valores en la tarjeta
            binding.LastSensorValue.text = "${listanueva[lastValue]?.hum2?.toFloat()} %"
            binding.sensorType.text = "Humidity"
            binding.lastTimeActualization.text = "Act. ${dateFormatedtwo.format(listanueva[lastValue]?.time)} hs"

            //Cargo los datos dentro de un array en orden y con su indice
            listanueva.forEach { it ->
                //Formateo el tiempo
                val timestamp = it?.time!!.time
                val index = timestamp.toFloat()

                hum1Entry.add(Entry(index, it?.hum2.toString().toFloat()))

            }

            //Inicializo el Linear Chart
            setLineChartData(hum1Entry, false)
        }

        if (listanueva[0]?.hum3 != null) {

            var hum1Entry = ArrayList<Entry>()
            var lastValue = listanueva.size - 1

            //Cargo los datos en de los ultimos valores en la tarjeta
            binding.LastSensorValue.text = "${listanueva[lastValue]?.hum3?.toFloat()} %"
            binding.sensorType.text = "Humidity"
            binding.lastTimeActualization.text = "Act. ${dateFormatedtwo.format(listanueva[lastValue]?.time)} hs"

            //Cargo los datos dentro de un array en orden y con su indice
            listanueva.forEach { it ->
                //Formateo el tiempo
                val timestamp = it?.time!!.time
                val index = timestamp.toFloat()

                hum1Entry.add(Entry(index, it?.hum3.toString().toFloat()))

            }

            //Inicializo el Linear Chart
            setLineChartData(hum1Entry, false)
        }

        // Reviso si el listado es un listado de Temperatura
        if (listanueva[0]?.temp1 != null) {

            var entry = ArrayList<Entry>()
            var lastValue = listanueva.size - 1

            //Cargo los datos en de los ultimos valores en la tarjeta
            binding.LastSensorValue.text = "${listanueva[lastValue]?.temp1?.toFloat()} °C"
            binding.sensorType.text = "Temperature"
            binding.lastTimeActualization.text = "Act. ${dateFormatedtwo.format(listanueva[lastValue]?.time)} hs"

            //Cargo los datos dentro de un array en orden y con su indice
            listanueva.forEach { it ->
                //Formateo el tiempo
                val timestamp = it?.time!!.time
                val index = timestamp.toFloat()
                entry.add(Entry(index, it?.temp1.toString().toFloat()))
            }

            //Inicializo el Linear Chart
            setLineChartData(entry, false)
        }

        if (listanueva[0]?.temp2 != null) {

            var entry = ArrayList<Entry>()
            var lastValue = listanueva.size - 1

            //Cargo los datos en de los ultimos valores en la tarjeta
            binding.LastSensorValue.text = "${listanueva[lastValue]?.temp2?.toFloat()} °C"
            binding.sensorType.text = "Temperature"
            binding.lastTimeActualization.text = "Act. ${dateFormatedtwo.format(listanueva[lastValue]?.time)} hs"

            //Cargo los datos dentro de un array en orden y con su indice
            listanueva.forEach { it ->
                //Formateo el tiempo
                val timestamp = it?.time!!.time
                val index = timestamp.toFloat()
                entry.add(Entry(index, it?.temp2.toString().toFloat()))
            }

            //Inicializo el Linear Chart
            setLineChartData(entry, false)
        }

        if (listanueva[0]?.temp3 != null) {

            var entry = ArrayList<Entry>()
            var lastValue = listanueva.size - 1

            //Cargo los datos en de los ultimos valores en la tarjeta
            binding.LastSensorValue.text = "${listanueva[lastValue]?.temp3?.toFloat()} °C"
            binding.sensorType.text = "Temperature"
            binding.lastTimeActualization.text = "Act. ${dateFormatedtwo.format(listanueva[lastValue]?.time)} hs"

            //Cargo los datos dentro de un array en orden y con su indice
            listanueva.forEach { it ->
                //Formateo el tiempo
                val timestamp = it?.time!!.time
                val index = timestamp.toFloat()
                entry.add(Entry(index, it?.temp3.toString().toFloat()))
            }

            //Inicializo el Linear Chart
            setLineChartData(entry, false)
        }

        // Reviso si el listado es un listado de switch
        if (listanueva[0]?.switch1 != null) {

            var entry = ArrayList<Entry>()
            var lastValue = listanueva.size - 1

            //Cargo los datos en de los ultimos valores en la tarjeta
            binding.LastSensorValue.text = "${listanueva[lastValue]?.switch1}"
            binding.sensorType.text = "Switch"
            binding.lastTimeActualization.text = "Act. ${dateFormatedtwo.format(listanueva[lastValue]?.time)} hs"
            if (listanueva[lastValue]?.switch1 == "true"){
                binding.cardInfo.setBackgroundResource(R.color.green)
            }else{
                binding.cardInfo.setBackgroundResource(R.color.red)
            }

            //Cargo los datos dentro de un array en orden y con su indice
            listanueva.forEach{it ->
                //Formateo el tiempo
                val timestamp = it?.time!!.time
                var index = timestamp.toFloat()
                val yValue = it?.switch1.toString()
                var yValueToFloat = 0

                if (yValue == "true"){
                    yValueToFloat = 1
                }else{
                    yValueToFloat = 0
                }

                entry.add(Entry(index, yValueToFloat.toFloat()))

            }

            //Inicializo el Linear Chart
            setLineChartData(entry, true)


        }

        if (listanueva[0]?.switch2 != null) {

            var entry = ArrayList<Entry>()
            var lastValue = listanueva.size - 1

            //Cargo los datos en de los ultimos valores en la tarjeta
            binding.LastSensorValue.text = "${listanueva[lastValue]?.switch2}"
            binding.sensorType.text = "Switch"
            binding.lastTimeActualization.text = "Act. ${dateFormatedtwo.format(listanueva[lastValue]?.time)} hs"
            if (listanueva[lastValue]?.switch2 == "true"){
                binding.cardInfo.setBackgroundResource(R.color.green)
            }else{
                binding.cardInfo.setBackgroundResource(R.color.red)
            }

            //Cargo los datos dentro de un array en orden y con su indice
            listanueva.forEach{it ->
                //Formateo el tiempo
                val timestamp = it?.time!!.time
                var index = timestamp.toFloat()
                val yValue = it?.switch1.toString()
                var yValueToFloat = 0

                if (yValue == "true"){
                    yValueToFloat = 1
                }else{
                    yValueToFloat = 0
                }

                entry.add(Entry(index, yValueToFloat.toFloat()))

            }

            //Inicializo el Linear Chart
            setLineChartData(entry, true)

        }

        if (listanueva[0]?.switch3 != null) {

            var entry = ArrayList<Entry>()
            var lastValue = listanueva.size - 1

            //Cargo los datos en de los ultimos valores en la tarjeta
            binding.LastSensorValue.text = "${listanueva[lastValue]?.switch3}"
            binding.sensorType.text = "Switch"
            binding.lastTimeActualization.text = "Act. ${dateFormatedtwo.format(listanueva[lastValue]?.time)} hs"
            if (listanueva[lastValue]?.switch3 == "true"){
                binding.cardInfo.setBackgroundResource(R.color.green)
            }else{
                binding.cardInfo.setBackgroundResource(R.color.red)
            }

            //Cargo los datos dentro de un array en orden y con su indice
            listanueva.forEach{it ->
                //Formateo el tiempo
                val timestamp = it?.time!!.time
                var index = timestamp.toFloat()
                val yValue = it?.switch1.toString()
                var yValueToFloat = 0

                if (yValue == "true"){
                    yValueToFloat = 1
                }else{
                    yValueToFloat = 0
                }

                entry.add(Entry(index, yValueToFloat.toFloat()))

            }

            //Inicializo el Linear Chart
            setLineChartData(entry, true)

        }
    }

    class XAxisTimeFormatterTwo: ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(value.toLong()))
        }
    }
}