package com.example.dataloggerextended.ui.view

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.dataloggerextended.R

import com.example.dataloggerextended.databinding.ActivityAddDeviceBinding
import com.example.dataloggerextended.utils.setLineColor
import com.example.dataloggerextended.utils.setTextColor

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddDeviceActivity : AppCompatActivity() {

    private val dbDevices = Firebase.firestore.collection("users")
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityAddDeviceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Recupero valor del Activity anterior
        val extras: Bundle? = intent.getBundleExtra("extras")
        val devId: String? = extras?.getString("device")
        val devModel: String? = extras?.getString("model")

        // Adapto el UI para que solo muestre los fields que le corresponden
        adaptUiForDeviceModel(devModel)

        // Cambiar el color del SupportActionBar
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.light_black)))
        supportActionBar!!.title = "New Device Settings"

        //Agrego un boton de regreso al MainActivity y cambio el titulo del action bar
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Cargo los datos ficticios dentro de los arrayLists
        var xvalueTime = ArrayList<String>()
        var entry = ArrayList<Entry>()
        cargoDatosFicticios(xvalueTime, entry)

        var lineDataSet = setLineChartData(xvalueTime, entry)

        binding.addNewDevice.setOnClickListener {
            addNewDeviceOnFirebase(devModel, devId)
        }

        setTextColorOnAddDevice()
        setLineColorOnAddDevice(lineDataSet)
    }

    private fun adaptUiForDeviceModel(model : String?){
        if (model == "esp32/h/t"){
            binding.etVoltName.visibility = View.GONE
            binding.etSwitchName.visibility = View.GONE
        }
    }

    private fun addNewDeviceOnFirebase(model: String?, device: String?){
        if (checkIfNotEmpty(model)){
            val docData =hashMapOf(
                "devName" to binding.etDeviceName.text.toString(),
                "devHum" to binding.etHumName.text.toString(),
                "devTemp" to binding.etTempName.text.toString(),
                "lineColor" to binding.lnColorSelectedTop.background.toString(),
                "textColor" to binding.txColorSelectedTop.background.toString())

            dbDevices.document(firebaseAuth.currentUser!!.email!!).collection("linked_devices").document(device!!).set(docData)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)}
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }else{
            Toast.makeText(this, "Ups, something is missing. Complete all the fields and try again!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun checkIfNotEmpty(model : String?): Boolean{
        if (model == "esp32/h/t"){
            val devName = binding.etDeviceName.text.toString()
            val devHum = binding.etHumName.text.toString()
            val devTemp = binding.etTempName.text.toString()
            val lineColor = binding.lnColorSelectedTop.background.toString()
            val textColor = binding.txColorSelectedTop.background.toString()
            if (devHum.isNotEmpty() && devTemp.isNotEmpty() && devName.isNotEmpty()
                && lineColor != "android.graphics.drawable.ColorDrawable@9faff0"
                && textColor != "android.graphics.drawable.ColorDrawable@9faff0"){
                return true
            }
        }
        return false
    }

    private fun cargoDatosFicticios(xvalueTime: ArrayList<String>, entry: ArrayList<Entry>){
        xvalueTime.add("14:09")
        xvalueTime.add("14:10")
        xvalueTime.add("14:11")
        xvalueTime.add("14:12")
        entry.add(Entry(0f, 10f))
        entry.add(Entry(1f, 20f))
        entry.add(Entry(20f, 30f))
        entry.add(Entry(3f, 40f))
    }

    //Inicializo el Linear Chart
    private fun setLineChartData(xvalueTime: ArrayList<String>, entry: List<Entry>): LineDataSet {


        val linedataset = LineDataSet(entry, "value")

        styleLineDataSet(linedataset)

        val finaldataset = ArrayList<LineDataSet>()


        finaldataset.add(linedataset)
        val data = LineData(finaldataset as List<ILineDataSet>)
        binding.lineChart.data = data

        styleChart(binding.lineChart)
        return linedataset
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

        setDrawFilled(true)
        fillDrawable = binding.root.resources.getDrawable(R.drawable.bg_shadow_graph_line_gold)

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setLineColorOnAddDevice(lineDataSet: LineDataSet){

        binding.lnColor1.setLineColor(
            R.color.blue,
            R.color.blue, binding.root.resources.getDrawable(R.drawable.bg_shadow_graph_line_blue), lineDataSet, binding)

        binding.lnColor2.setLineColor(
            R.color.purple,
            R.color.purple, binding.root.resources.getDrawable(R.drawable.bg_shadow_graph_line_purple), lineDataSet, binding)

        binding.lnColor3.setLineColor(
            R.color.pink,
            R.color.pink, binding.root.resources.getDrawable(R.drawable.bg_shadow_graph_line_pink), lineDataSet, binding)

        binding.lnColor4.setLineColor(
            R.color.peach,
            R.color.peach, binding.root.resources.getDrawable(R.drawable.bg_shadow_graph_line_peach), lineDataSet, binding)

        binding.lnColor5.setLineColor(
            R.color.orange,
            R.color.orange, binding.root.resources.getDrawable(R.drawable.bg_shadow_graph_line_orange), lineDataSet, binding)

        binding.lnColor6.setLineColor(
            R.color.gold,
            R.color.gold, binding.root.resources.getDrawable(R.drawable.bg_shadow_graph_line_gold), lineDataSet, binding)

        binding.lnColor7.setLineColor(
            R.color.neon_purple,
            R.color.neon_purple, binding.root.resources.getDrawable(R.drawable.bg_shadow_graph_line_purple), lineDataSet, binding)

        binding.lnColor8.setLineColor(
            R.color.green,
            R.color.green, binding.root.resources.getDrawable(R.drawable.bg_shadow_graph_line_green), lineDataSet, binding)

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setTextColorOnAddDevice() {

        binding.txColor1.setTextColor(binding.root.resources.getDrawable(R.color.white), binding.root.resources.getColor(
            R.color.white
        ),  binding)

        binding.txColor2.setTextColor(binding.root.resources.getDrawable(R.color.pink), binding.root.resources.getColor(
            R.color.pink
        ), binding)

        binding.txColor3.setTextColor(binding.root.resources.getDrawable(R.color.green), binding.root.resources.getColor(
            R.color.green
        ), binding)

        binding.txColor4.setTextColor(binding.root.resources.getDrawable(R.color.gold), binding.root.resources.getColor(
            R.color.gold
        ), binding)

    }

}