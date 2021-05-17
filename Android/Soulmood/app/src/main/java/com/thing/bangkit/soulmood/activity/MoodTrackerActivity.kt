package com.thing.bangkit.soulmood.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.databinding.ActivityMoodTrackerBinding
import com.thing.bangkit.soulmood.helper.DateHelper
import com.thing.bangkit.soulmood.viewmodel.MoodTrackerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class MoodTrackerActivity : AppCompatActivity() {
    private var binding: ActivityMoodTrackerBinding? = null
    private val moodTrackerViewModel: MoodTrackerViewModel by viewModels()

    //mood code
    private val angry = ArrayList<String>()
    private val sad = ArrayList<String>()
    private val fear = ArrayList<String>()
    private val good = ArrayList<String>()
    private val love = ArrayList<String>()
    private val joy = ArrayList<String>()

    private val lineChartArrayEntry = ArrayList<Entry>()
    private val dateList = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoodTrackerBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        supportActionBar?.title = getString(R.string.your_mood_recap)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        moodTrackerViewModel.setMoodData(DateHelper.getCurrentDateTime(), this)

        moodTrackerViewModel.setMoodData(DateHelper.getCurrentDateTime(), this)
        moodTrackerViewModel.getMoodData().observe(this, {
            lineChartArrayEntry.clear()
            dateList.clear()
            angry.clear()
            joy.clear()
            good.clear()
            sad.clear()
            fear.clear()
            love.clear()
            if(it.isNotEmpty()){
                CoroutineScope(Dispatchers.Default).launch {
                    for (i in 0 until it.size) {
                        lineChartArrayEntry.add(Entry(i.toFloat(), it[i].mood_code.toFloat()))
                        dateList.add(it[i].date.substring(8, 10))
                        when (it[i].mood_code) {
                            "1" -> angry.add(it[i].mood_code)
                            "2" -> sad.add(it[i].mood_code)
                            "3" -> fear.add(it[i].mood_code)
                            "4" -> good.add(it[i].mood_code)
                            "5" -> love.add(it[i].mood_code)
                            "6" -> joy.add(it[i].mood_code)
                        }
                    }
                }

                CoroutineScope(Dispatchers.Main).launch {
                    delay(300)
                    lineChart()
                }
            }else{
                binding?.apply {
                    lineChart.visibility = View.GONE
                    noDataAnimation.visibility = View.VISIBLE
                }
            }

            CoroutineScope(Dispatchers.Main).launch {
                delay(300)
                binding?.apply {
                    tvJoyScore.text = joy.size.toString()
                    tvLoveScore.text = love.size.toString()
                    tvGoodScore.text = good.size.toString()
                    tvFearScore.text = fear.size.toString()
                    tvSadScore.text = sad.size.toString()
                    tvAngryScore.text = angry.size.toString()
                }
            }
        })

        binding?.apply {
            tvMoodDate.text = DateHelper.convertDateToMonthYearFormat(DateHelper.getCurrentDateTime().take(7))
            floatingFilterByDate.setOnClickListener { datePicker() }
        }

    }

    private fun lineChart() {
        binding?.apply {
            noDataAnimation.visibility = View.GONE
            lineChart.visibility = View.VISIBLE
        }
        val lineDataSet = LineDataSet(lineChartArrayEntry, "Mood Harianmu")
        lineDataSet.notifyDataSetChanged()
        val color = ContextCompat.getColor(this, R.color.soulmood_primary_color)
        lineDataSet.color = color
        lineDataSet.lineWidth = 4f
        lineDataSet.valueTextSize = 0f
        lineDataSet.circleRadius = 6f
        lineDataSet.setCircleColor(color)
        lineDataSet.circleHoleColor = color

        val desc = Description()
        desc.text = ""
        binding?.apply {

            lineChart.notifyDataSetChanged()
            lineChart.invalidate()
            lineChart.description = desc

            var lineData = LineData(lineDataSet)



            lineChart.data = lineData
            val xAxis: XAxis = lineChart.xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(dateList)
            lineChart.setVisibleYRange(1f,6f, YAxis.AxisDependency.LEFT)
            val yAxis : YAxis = lineChart.getAxis(YAxis.AxisDependency.LEFT)
            yAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("","Marah", "Takut", "Sedih", "Baik", "Asmara", "Bahagia"))


            //remove line in right side
            lineChart.axisRight.isEnabled = false

            //auto zoom
            //lineChart.setScaleMinima(1.5f, 1f)

            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(false)
            xAxis.granularity = 1f
            xAxis.labelCount = dateList.size
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            lineChart.animate()




        }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun datePicker() {
        val title = "Filter Mood"
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dialogFragment = MonthYearPickerDialogFragment.getInstance(month, year, title)
        dialogFragment.show(supportFragmentManager, null)
        dialogFragment.setOnDateSetListener { year, monthOfYear ->
            moodTrackerViewModel.setMoodData("$year-0" + (monthOfYear + 1), this)
            binding?.apply{
                tvMoodDate.text = DateHelper.convertDateToMonthYearFormat("$year-0" + (monthOfYear + 1))
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}