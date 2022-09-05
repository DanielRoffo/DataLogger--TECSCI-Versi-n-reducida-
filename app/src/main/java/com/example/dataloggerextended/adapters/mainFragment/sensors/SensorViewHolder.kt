package com.example.dataloggerextended.adapters.mainFragment.sensors

import android.annotation.SuppressLint
import android.view.View
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

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    fun render(sensorModel: List<DeviceData?>, onClickListener: (List<DeviceData?>) -> Unit) {

        val dateFormated = SimpleDateFormat("HH:mm")
        val dateFormatedtwo = SimpleDateFormat("dd/MM/yy || HH:mm:ss")

        // Ordeno los listados de los sensores por Dia y Hora
        var listanueva = sensorModel.sortedBy { it?.time }

        // Reviso si el listado es un listado de Humedad
        if (listanueva[0]?.hum != null) {

            var xvalueTime = ArrayList<String>()
            var hum1Entry = ArrayList<Entry>()
            var lastValue = listanueva.size - 1

            //Cargo los datos en de los ultimos valores en la tarjeta
            binding.LastSensorValue.text = "${listanueva[lastValue]?.hum?.toFloat()} %"
            binding.sensorType.text = "Humidity"
            binding.lastTimeActualization.text = "Act. ${dateFormatedtwo.format(listanueva[lastValue]?.time)} hs"

            //Cargo los datos dentro de un array en orden y con su indice
            listanueva.forEach { it ->
                //Formateo el tiempo
                val timestamp = it?.time!!.time
                var dateToString = dateFormatedtwo.format(timestamp)
                val index = timestamp.toFloat()

                hum1Entry.add(Entry(index, it?.hum.toString().toFloat()))

            }

            //Inicializo el Linear Chart
            setLineChartData(hum1Entry)
        }


        // Reviso si el listado es un listado de Temperatura
        if (sensorModel[0]?.temp != null) {
            var xvalueTime = ArrayList<String>()
            var entry = ArrayList<Entry>()
            var lastValue = listanueva.size - 1

            //Cargo los datos en de los ultimos valores en la tarjeta
            binding.LastSensorValue.text = "${listanueva[lastValue]?.temp?.toFloat()} °C"
            binding.sensorType.text = "Temperature"
            binding.lastTimeActualization.text = "Act. ${dateFormatedtwo.format(listanueva[lastValue]?.time)} hs"

            //Cargo los datos dentro de un array en orden y con su indice
            listanueva.forEach { it ->
                //Formateo el tiempo
                val timestamp = it?.time!!.time
                var dateToString = dateFormatedtwo.format(timestamp)
                val index = timestamp.toFloat()
                entry.add(Entry(index, it?.temp.toString().toFloat()))
            }

            //Inicializo el Linear Chart
            setLineChartData(entry)
        }

        binding.cardInfo.setOnClickListener {
            onClickListener(sensorModel)
        }
    }

    //Inicializo el Linear Chart
    private fun setLineChartData(entry: List<Entry>) {


        val linedataset = LineDataSet(entry, "value")

        styleLineDataSet(linedataset)

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
    private fun styleLineDataSet(lineDataSet: LineDataSet) = lineDataSet.apply {
        color = binding.root.resources.getColor(R.color.gold)
        valueTextColor = binding.root.resources.getColor(R.color.gold)
        setDrawValues(false)
        lineWidth = 4f
        isHighlightEnabled = true
        setDrawHighlightIndicators(false)
        setDrawCircles(false)

        setDrawFilled(true)
        fillDrawable = binding.root.resources.getDrawable(R.drawable.bg_shadow_graph_line_gold)

    }

    class XAxisTimeFormatterTwo: ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(value.toLong()))
        }
    }
}