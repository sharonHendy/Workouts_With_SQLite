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
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter

import com.github.mikephil.charting.utils.ViewPortHandler
import com.google.gson.Gson
import com.workouts.DBHelper.DBHelper
import com.workouts.DTOs.WeekPerformance
import kotlinx.android.synthetic.main.fragment_charts.*
import java.text.DecimalFormat

class chartsFragment : Fragment() {

    lateinit var db : DBHelper
    lateinit var colors:ArrayList<Int>
   // var showLastWeek :Boolean = false
    var numOfWeek: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        db = DBHelper(requireContext())
        // Inflate the layout for this fragment
        val v : View = inflater.inflate(R.layout.fragment_charts, container, false)
        //colors
        colors = ArrayList<Int>()
        colors.add(ContextCompat.getColor(requireContext(),R.color.pastel_blue))
        colors.add(ContextCompat.getColor(requireContext(),R.color.pastel_green))
        colors.add(ContextCompat.getColor(requireContext(),R.color.pastel_purple))
        colors.add(ContextCompat.getColor(requireContext(),R.color.pastel_pink))
        colors.add(ContextCompat.getColor(requireContext(),R.color.pastel_grey))

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        var weekPerformance = db.WEEK_PERFORMANCES.getWeekPerformance( 1)
        setBarChartValues(weekPerformance)

        setPieChartValues()

        btnLastWeek.setOnClickListener { //todo 4 weeks..
            if(numOfWeek < 4){
                numOfWeek += 1
                setBarChartValues(db.WEEK_PERFORMANCES.getWeekPerformance(numOfWeek))
            }
//            if (!showLastWeek){
//                setBarChartValues(db.WEEK_PERFORMANCES.getWeekPerformance(2))
//                btnLastWeek.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.arrow_forward)
//            }else{
//                setBarChartValues(db.WEEK_PERFORMANCES.getWeekPerformance(1))
//                btnLastWeek.setCompoundDrawablesWithIntrinsicBounds(0,0,0,R.drawable.arrow_back)
//            }
//            showLastWeek = !showLastWeek
        }

        btnNextWeek.setOnClickListener {
            if(numOfWeek > 1){
                numOfWeek -= 1
                setBarChartValues(db.WEEK_PERFORMANCES.getWeekPerformance(numOfWeek))
            }
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
//            if(showLastWeek){
//                setBarChartValues(db.WEEK_PERFORMANCES.getWeekPerformance(2))
//            }else{
//                setBarChartValues(db.WEEK_PERFORMANCES.getWeekPerformance( 1))
//            }
            setBarChartValues(db.WEEK_PERFORMANCES.getWeekPerformance(numOfWeek))
        }

        btnRefreshPieChart.setOnClickListener {
            setPieChartValues()
        }


    }

    /**
     * clears the charts data.
     */
    fun clearHistory(){
        db.WEEK_PERFORMANCES.clearData()
        db.WORKOUTS.resetTimePlayed()
        numOfWeek = 1
        setBarChartValues(db.WEEK_PERFORMANCES.getWeekPerformance(1))
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

        val listOfWorkouts = db.WORKOUTS.getAllWorkouts()
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
        pieDataSet.color = ContextCompat.getColor(requireContext(),R.color.light_gray)
//        pieDataSet.color = resources.getColor(R.color.light_gray)
        pieDataSet.sliceSpace = 4f
        pieDataSet.colors = colors
        pieDataSet.valueTextSize = 14f

        pieDataSet.valueFormatter = MyValueFormatter()

        val data = PieData(xValues,pieDataSet)
        pieChart.data = data

        pieChart.holeRadius = 40f
        pieChart.setHoleColor(ContextCompat.getColor(requireContext(),R.color.grey3))
        pieChart.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.grey3))
        pieChart.setDescription("")
        //pieChart.animateY(2000)
        pieChart.animateXY(2000,2000)

        val legend : Legend = pieChart.legend
        legend.position = Legend.LegendPosition.LEFT_OF_CHART
        legend.textColor = ContextCompat.getColor(requireContext(),R.color.white)

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


    fun setBarChartValues(weekPerformance : WeekPerformance){
        val xvalues : ArrayList<String> = ArrayList<String>()
        xvalues.add("SUN")
        xvalues.add("MON")
        xvalues.add("TUE")
        xvalues.add("WEN")
        xvalues.add("THU")
        xvalues.add("FRI")
        xvalues.add("SAT")

        val barEntries = ArrayList<BarEntry>()
        barEntries.add(BarEntry(weekPerformance.SUN,0))
        barEntries.add(BarEntry(weekPerformance.MON,1))
        barEntries.add(BarEntry(weekPerformance.TUS,2))
        barEntries.add(BarEntry(weekPerformance.WED,3))
        barEntries.add(BarEntry(weekPerformance.THU,4))
        barEntries.add(BarEntry(weekPerformance.FRI,5))
        barEntries.add(BarEntry(weekPerformance.SAT,6))

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