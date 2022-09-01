package com.example.dataloggerextended.utils

import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.dataloggerextended.databinding.ActivityAddDeviceBinding
import com.github.mikephil.charting.data.LineDataSet

fun CardView.setLineColor(@ColorRes color:Int, @DrawableRes drawable: Int, shadowDrawable: Drawable , lineDataSet: LineDataSet, binding: ActivityAddDeviceBinding){
    setOnClickListener {
        binding.lnColorSelectedTop.background = ContextCompat.getDrawable(context, drawable)
        lineDataSet.color = binding.root.resources.getColor(color)
        lineDataSet.fillDrawable = shadowDrawable
        lineDataSet.notifyDataSetChanged()
        binding.lineChart.notifyDataSetChanged()
        binding.lineChart.invalidate()
    }
}

fun CardView.setTextColor(drawable: Drawable, textColor: Int , binding: ActivityAddDeviceBinding) {
    setOnClickListener {
        binding.txColorSelectedTop.background = drawable
        binding.lineChart.axisLeft.setTextColor(textColor)
        binding.lineChart.axisRight.setTextColor(textColor)
        binding.lineChart.xAxis.setTextColor(textColor)
        binding.lineChart.legend.setTextColor(textColor)
        binding.lineChart.notifyDataSetChanged()
        binding.lineChart.invalidate()
    }
}