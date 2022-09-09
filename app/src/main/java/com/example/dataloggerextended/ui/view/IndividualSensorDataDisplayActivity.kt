package com.example.dataloggerextended.ui.view

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dataloggerextended.R
import com.example.dataloggerextended.adapters.indSensorData.IndSensorDataAdapter
import com.example.dataloggerextended.databinding.ActivityIndividualSensorDataDisplayBinding
import com.example.dataloggerextended.model.SensorData
import com.example.dataloggerextended.ui.viewmodel.IndividualSensorViewModel
import com.example.dataloggerextended.utils.ScreenState
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class IndividualSensorDataDisplayActivity : AppCompatActivity() {

    //Inicializo el ViewModel
    private val viewModel: IndividualSensorViewModel by lazy {
        ViewModelProvider(this).get(IndividualSensorViewModel::class.java)
    }

    private val myAdapter by lazy  {
        IndSensorDataAdapter(emptyList<SensorData?>())
    }

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityIndividualSensorDataDisplayBinding
    val simpleDateFormated = SimpleDateFormat("dd/MM/yy || HH:mm:ss")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_individual_sensor_data_display)

        val data = intent.getSerializableExtra("data")
        var dataAsArray = data as ArrayList<String>


        // Cambiar el color del SupportActionBar
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.light_black)))

        //Agrego un boton de regreso al MainActivity y cambio el titulo del action bar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "${dataAsArray[0]} - ${dataAsArray[1]}"

        binding = ActivityIndividualSensorDataDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()

        initRecyclerView()

        //Observo del MainViewModel si hay cambios en los datos de individualDataSet
        viewModel.individualDataSet.observe(this ,{ state ->
            processDevicesResponse(state)
        })




    }

    //Activa o desactiva las vistas de loading dependiendo del estado de los datos recibidos
    private fun processDevicesResponse(state: ScreenState<List<SensorData?>>) {
        when (state) {
            is ScreenState.Loading -> {
                //Activo el loading
                binding.viewLoading1.visibility = View.VISIBLE
                binding.viewLoading2.visibility = View.VISIBLE
            }
            is ScreenState.Success -> {

                var yvalue = mutableListOf<Entry>()
                var isStepped: Boolean = false
                var organizedList = state.data?.sortedByDescending { it?.time }
                if (organizedList != null) {
                    myAdapter?.setData(organizedList)
                }
                organizedList = state.data?.sortedBy { it?.time }
                var xOld = Timestamp(System.currentTimeMillis()).time
                organizedList?.forEach() {it ->
                    //Formateo el tiempo

                    val timestamp = it?.time!!.time
                    var dateToString = simpleDateFormated.format(timestamp)
                    val index = timestamp.toFloat()

                    if (it?.sensorVal == "true" || it?.sensorVal == "false"){
                        if (it?.sensorVal == "true"){
                            yvalue.add(Entry(index, 1F))
                        }else{
                            yvalue.add(Entry(index, 0F))
                        }
                        isStepped = true

                    }else{
                        yvalue.add(Entry(index, it?.sensorVal.toString().toFloat()))
                    }



                }
                setLineChartData(yvalue, isStepped)
                binding.viewLoading1.visibility = View.GONE
                binding.viewLoading2.visibility = View.GONE
                binding.lineChart.visibility = View.VISIBLE
                binding.singleSensorRv.visibility = View.VISIBLE

            }
            is ScreenState.Error -> {
                binding.viewLoading1.visibility = View.GONE
                binding.viewLoading2.visibility = View.GONE
                val view = binding.viewLoading1.rootView
                Snackbar.make(view, state.message!!, Snackbar.LENGTH_INDEFINITE).show()
            }
        }
    }

    private fun getData(){

        val data = intent.getSerializableExtra("data")
        var dataAsArray = data as ArrayList<String>
        viewModel.getDataFromSensor(dataAsArray[0], dataAsArray[1])

    }

    //Se inicializa el RecyclerView
    private fun initRecyclerView() {
        val recyclerView = binding.singleSensorRv
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = myAdapter

    }

    //Inicializo el Linear Chart
    private fun setLineChartData(entry: List<Entry>, isStepped: Boolean) {


        val linedataset = LineDataSet(entry, "value")

        styleLineDataSet(linedataset, isStepped)

        val finaldataset = ArrayList<LineDataSet>()


        finaldataset.add(linedataset)
        val data = LineData(finaldataset as List<ILineDataSet>)
        binding.lineChart.data = data

        binding.lineChart.xAxis.valueFormatter = XAxisTimeFormatter()

        binding.lineChart.marker = object : MarkerView(this, R.layout.custom_marker_view) {
            override fun refreshContent(e: Entry, highlight: Highlight) {
                (findViewById<View>(R.id.tvContent) as TextView).text = "${e.y} at ${simpleDateFormated.format(e.x)}"
            }

            override fun draw(canvas: Canvas, posX: Float, posY: Float) {
                if (posX > canvas.width / 2.0) //Check if the user is in the right half of the canvas
                    super.draw(
                        canvas,
                        100f,
                        50f
                    ) else  //Otherwise draw the marker on the top right corner.
                    super.draw(canvas, 100f, 50f)
            }
        }

        styleChart(binding.lineChart)
    }

    //Configuro el Chart
    private fun styleChart(lineChart: LineChart) = lineChart.apply {

        setTouchEnabled(true)
        isDragEnabled = true
        setScaleEnabled(true)

        setDescription(null)
        legend.isEnabled = false
        setNoDataText("Sensor not initialize")
        setDrawGridBackground(false)
        setDrawBorders(false)


        setBackgroundColor(binding.root.resources.getColor(R.color.light_black))
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

        axisLeft.isEnabled = true
        axisLeft.mAxisMinimum = 0f
        axisLeft.mAxisMaximum = 10f

        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM

    }

    //Configuro la linea del Chart
    private fun styleLineDataSet(lineDataSet: LineDataSet, isStepped: Boolean) = lineDataSet.apply {
        color = binding.root.resources.getColor(R.color.gold)
        valueTextColor = binding.root.resources.getColor(R.color.gold)
        setDrawValues(false)
        lineWidth = 4f
        isHighlightEnabled = true
        setDrawHighlightIndicators(true)
        setDrawCircles(true)
        setCircleColor(binding.root.resources.getColor(R.color.gold))

        if (isStepped){
            mode = LineDataSet.Mode.STEPPED
        }

        setDrawFilled(true)
        fillDrawable = binding.root.resources.getDrawable(R.drawable.bg_shadow_graph_line_gold)

    }

    class XAxisTimeFormatter: ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return SimpleDateFormat("dd/MM || HH:mm", Locale.getDefault()).format(Date(value.toLong()))
        }
    }


}