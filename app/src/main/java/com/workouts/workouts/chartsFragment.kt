package com.workouts.workouts

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter

import com.github.mikephil.charting.utils.ViewPortHandler
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_charts.*
import java.text.DecimalFormat

class chartsFragment : Fragment() {

    lateinit var colors:ArrayList<Int>
    var showLastWeek :Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v : View = inflater.inflate(R.layout.fragment_charts, container, false)
        //colors
        colors = ArrayList<Int>()
        colors.add(resources.getColor(R.color.pastel_blue))
        colors.add(resources.getColor(R.color.pastel_green))
        colors.add(resources.getColor(R.color.pastel_purple))
        colors.add(resources.getColor(R.color.pastel_pink))
        colors.add(resources.getColor(R.color.pastel_grey))

        //v.findViewById<BarChart>(R.id.barChart).invalidate();
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBarChartValues(getWeekPerformance1(requireActivity()))
        setPieChartValues()

        btnLastWeek.setOnClickListener {
            if (!showLastWeek){
                setBarChartValues(getWeekPerformance2(requireActivity()))
                btnLastWeek.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.arrow_forward)
            }else{
                setBarChartValues(getWeekPerformance1(requireActivity()))
                btnLastWeek.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.arrow_back)
            }
            showLastWeek = !showLastWeek
        }

        btnClearHistory.setOnClickListener {
            showDelHistoryDialog()
        }

//        val scrollBounds = Rect()
//        scrollView1.getHitRect(scrollBounds)
//        if(pieChart.getLocalVisibleRect(scrollBounds)){
//            setPieChartValues()
//        }


        btnRefreshBarChart.setOnClickListener {
            if(showLastWeek){
                setBarChartValues(getWeekPerformance2(requireActivity()))
            }else{
                setBarChartValues(getWeekPerformance1(requireActivity()))
            }
        }

        btnRefreshPieChart.setOnClickListener {
            setPieChartValues()
        }


    }

    /**
     * clears the charts data.
     */
    fun clearHistory(){
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("weekPerformance1", null)

        val listOfWorkouts = getListOfWorkouts(requireActivity())
        for (workout in listOfWorkouts){
            workout.timePlayed = 0
        }
        val jsonStr = Gson().toJson(listOfWorkouts)
        editor.putString("ListOfWorkouts",jsonStr)

        editor.apply()

        val weekPerformance1 = getWeekPerformance1(requireActivity())
        setBarChartValues(weekPerformance1)
        setPieChartValues()
    }

    /**
     * opens delete dialog, clears history if the user chooses to.
     */
    fun showDelHistoryDialog(){
        val dialog = MaterialDialog(requireContext())
            .customView(R.layout.clear_history_dialog)
        dialog.findViewById<Button>(R.id.btnYes).setOnClickListener {
            clearHistory()
            dialog.cancel()
        }
        dialog.findViewById<Button>(R.id.btnNo).setOnClickListener {
            dialog.cancel()
        }
        dialog.show()
    }

    fun setPieChartValues(){

        //xvalues
        val xValues = ArrayList<String>()

        //yvalue
        val yValues = ArrayList<Float>()

        val listOfWorkouts = getListOfWorkouts(requireActivity())
        for (workout in listOfWorkouts){
            if (workout.timePlayed > 0){
                xValues.add(workout.name)
                yValues.add(workout.timePlayed.toFloat())
            }
        }

        val pieChartEntry = ArrayList<Entry>()
        for((i,item) in yValues.withIndex()){
            pieChartEntry.add(Entry(item, i))
        }

        //fill the chart
        val pieDataSet = PieDataSet(pieChartEntry,"")
        pieDataSet.color = resources.getColor(R.color.light_gray)
        pieDataSet.sliceSpace = 4f
        pieDataSet.colors = colors
        pieDataSet.valueTextSize = 14f

        pieDataSet.valueFormatter = MyValueFormatter()

        val data = PieData(xValues,pieDataSet)
        pieChart.data = data

        pieChart.holeRadius = 40f
        pieChart.setHoleColor(resources.getColor(R.color.grey3))
        pieChart.setBackgroundColor(resources.getColor(R.color.grey3))
        pieChart.setDescription("")
        //pieChart.animateY(2000)
        pieChart.animateXY(2000,2000)

        val legend : Legend = pieChart.legend
        legend.position = Legend.LegendPosition.LEFT_OF_CHART
        legend.textColor = resources.getColor(R.color.white)

        pieChart.invalidate()
  //      pieChart.setUsePercentValues(true)
//        pieChart.isDrawHoleEnabled = false
//        pieChart.setTouchEnabled(false)
    }

    class MyValueFormatter : ValueFormatter {
        private val format = DecimalFormat("###")
        // override this for e.g. LineChart or ScatterChart

        override fun getFormattedValue(
            value: Float,
            entry: Entry?,
            dataSetIndex: Int,
            viewPortHandler: ViewPortHandler?
        ): String {
            return format.format(value)
        }
    }


    fun setBarChartValues(weekPerformance : ArrayList<Float>){
        val xvalues : ArrayList<String> = ArrayList<String>()
        xvalues.add("SUN")
        xvalues.add("MON")
        xvalues.add("TUE")
        xvalues.add("WEN")
        xvalues.add("THU")
        xvalues.add("FRI")
        xvalues.add("SAT")

        val barEntries = ArrayList<BarEntry>()
        barEntries.add(BarEntry(weekPerformance[0].toFloat(),0))
        barEntries.add(BarEntry(weekPerformance[1].toFloat(),1))
        barEntries.add(BarEntry(weekPerformance[2].toFloat(),2))
        barEntries.add(BarEntry(weekPerformance[3].toFloat(),3))
        barEntries.add(BarEntry(weekPerformance[4].toFloat(),4))
        barEntries.add(BarEntry(weekPerformance[5].toFloat(),5))
        barEntries.add(BarEntry(weekPerformance[6].toFloat(),6))

        var barDataSet = BarDataSet(barEntries,"total time of workout")
        barDataSet.colors = colors
        barDataSet.setValueTextColor(Color.WHITE)
        barDataSet.valueTextSize = 16f
        val colors : MutableList<Int> = arrayListOf()

        barDataSet.valueFormatter = MyBarChartValueFormatter()

        var data = BarData(xvalues,barDataSet)

        //hide grid lines
        barChart.axisLeft.setDrawGridLines(false)
        barChart.xAxis.setDrawGridLines(false)
        barChart.xAxis.setDrawAxisLine(false)
        //remove right y-axis
        barChart.axisRight.isEnabled = false
        //remove legend
        barChart.legend.isEnabled = false
        //remove description label
        barChart.setDescription("")
        // to draw label on xAxis
        var xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.textColor = Color.WHITE
        //xAxis.valueFormatter = MyAxisFormatter()
        //xAxis.setDrawLabels(true)
        //xAxis.granularity = 1f
        //xAxis.labelRotationAngle = +90f

        barChart.data = data
        barChart.setBackgroundColor(resources.getColor(R.color.grey3))
        barChart.animateY(2000)

        barChart.invalidate()
    }

    class MyBarChartValueFormatter : ValueFormatter {
        //private val format = DecimalFormat("##:##:##")

        override fun getFormattedValue(
            value: Float,
            entry: Entry?,
            dataSetIndex: Int,
            viewPortHandler: ViewPortHandler?
        ): String {
            val minutes = value.toInt()
            val seconds = ((value - minutes) * 60).toInt()
            val hours = minutes / 60
            return "$hours:$minutes:$seconds"
        }
    }

}