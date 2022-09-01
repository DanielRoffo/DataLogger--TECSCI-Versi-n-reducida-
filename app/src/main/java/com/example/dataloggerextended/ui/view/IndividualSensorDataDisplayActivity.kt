package com.example.dataloggerextended.ui.view

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dataloggerextended.R
import com.example.dataloggerextended.adapters.indSensorData.IndSensorDataAdapter
import com.example.dataloggerextended.databinding.ActivityIndividualSensorDataDisplayBinding
import com.example.dataloggerextended.model.SensorData
import com.example.dataloggerextended.ui.viewmodel.IndividualSensorViewModel
import com.example.dataloggerextended.utils.ScreenState
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat

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
    val simpleDateFormated = SimpleDateFormat("HH:mm")

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
                var xvalueTime = ArrayList<String>()
                var yvalue = ArrayList<Entry>()
                var organizedList = state.data?.sortedByDescending { it?.time }
                if (organizedList != null) {
                    myAdapter?.setData(organizedList)
                }
                organizedList = state.data?.sortedBy { it?.time }
                organizedList?.forEachIndexed { index, it ->
                    //Formateo el tiempo

                    val timestamp = it?.time
                    var dateToString = simpleDateFormated.format(timestamp)

                    yvalue.add(Entry(it?.sensorVal.toString().toFloat(), index))
                    xvalueTime.add(dateToString)

                }
                setLineChartData(xvalueTime, yvalue)
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
    private fun setLineChartData(xvalueTime: ArrayList<String>, entry: List<Entry>) {


        val linedataset = LineDataSet(entry, "value")

        styleLineDataSet(linedataset)

        val finaldataset = ArrayList<LineDataSet>()


        finaldataset.add(linedataset)
        val data = LineData(xvalueTime, finaldataset as List<ILineDataSet>)
        binding.lineChart.data = data

        styleChart(binding.lineChart)
    }

    //Configuro el Chart
    private fun styleChart(lineChart: LineChart) = lineChart.apply {

        setTouchEnabled(true)
        isDragEnabled = true
        setScaleEnabled(true)
        setPinchZoom(true)

        setDescription(null)
        legend.isEnabled = false
        setNoDataText("Sensor not initialize")
        setDrawGridBackground(false)
        setDrawBorders(false)
        setMaxVisibleValueCount(10)

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

        xAxis.mAxisMinimum = 0f
        xAxis.mAxisMaximum = 24f
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM

    }

    //Configuro la linea del Chart
    private fun styleLineDataSet(lineDataSet: LineDataSet) = lineDataSet.apply {
        color = binding.root.resources.getColor(R.color.gold)
        valueTextColor = binding.root.resources.getColor(R.color.gold)
        setDrawValues(false)
        lineWidth = 4f
        isHighlightEnabled = true
        setDrawHighlightIndicators(true)
        setDrawCircles(false)
        setCircleColor(binding.root.resources.getColor(R.color.gold))

        setDrawCubic(false)

        setDrawFilled(true)
        fillDrawable = binding.root.resources.getDrawable(R.drawable.bg_shadow_graph_line_gold)

    }

}