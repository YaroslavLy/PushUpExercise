package com.yaroslav.pushupexercise.ui.statistics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.LineChart
import com.yaroslav.pushupexercise.databinding.FragmentStatisticsBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import android.graphics.Color
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import com.yaroslav.pushupexercise.R
import com.yaroslav.pushupexercise.appComponent
import com.yaroslav.pushupexercise.models.PushUpSum
import com.yaroslav.pushupexercise.models.PushUpSumMonth
import com.yaroslav.pushupexercise.models.PushUpSumYear
import com.yaroslav.pushupexercise.utils.*
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.roundToLong



enum class PeriodOptions {
    DAILY, WEEKLY, MONTHLY, YEARLY
}

class DetailsStatistics(
    var line1: String = "", var line2: String = "", var line3: String = ""
)

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private var periodOptions = PeriodOptions.DAILY

    private var dailyStatistics = DetailsStatistics()
    private var weeklyStatistics = DetailsStatistics()
    private var monthlyStatistics = DetailsStatistics()
    private var yearlyStatistics = DetailsStatistics()


    private val statisticsViewModel: StatisticsViewModel by viewModels {
        appComponent.statisticsViewModelsFactory()
    }

    override fun onResume() {
        super.onResume()
        initFilterAfterResume()//start dailyOptionTextView
        //initGraphData(periodOptions)//start dailyOptionTextView
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            statisticsViewModel.pushUpsSumDailyState.collect {
                if (it.isNotEmpty()) {
                    setGraphDaily(it.toMutableList())
                    updateStatisticsDetails(dailyStatistics)
                }

            }
        }

        lifecycleScope.launch {
            statisticsViewModel.pushUpsSumWeeklyState.collect {
                if (it.isNotEmpty()) {
                    setGraphWeekly(it.toMutableList())
                    updateStatisticsDetails(weeklyStatistics)
                }

            }
        }

        lifecycleScope.launch {
            statisticsViewModel.pushUpsSumMonthlyState.collect {
                if (it.isNotEmpty()) {
                    setGraphMonthly(it.toMutableList())
                    updateStatisticsDetails(monthlyStatistics)
                }

            }
        }
        //setGraphMonthly

        lifecycleScope.launch {
            statisticsViewModel.pushUpsSumYearlyState.collect {
                if (it.isNotEmpty()) {
                    setGraphYearly(it.toMutableList())
                    updateStatisticsDetails(yearlyStatistics)
                }

            }
        }

        optionsLayoutElementsColorSelected()

    }

    private fun updateStatisticsDetails(statistics: DetailsStatistics) {
        //todo add speakable naming
        binding.textLine1.text = statistics.line1
        binding.textLine2.text = statistics.line2
        binding.textLine3.text = statistics.line3
    }

    private fun setGraphDaily(pushUps: MutableList<PushUpSum>) {

        val lineChart: LineChart = binding.lineChartDaily

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val timeLineDayInSeconds = 86400000
        val startCurrentDay = calendar.timeInMillis
        val endCurrentDay = calendar.timeInMillis + timeLineDayInSeconds
        val newPushUps = mutableListOf<PushUpSum>()

        //if day cant record set 0 count
        (0..50).forEach { it1 ->
            pushUps.forEach { it2 ->
                val recordTime = (it2.recordTime).toLong() * 1000
                if ((startCurrentDay - (timeLineDayInSeconds * it1.toLong())) < (recordTime)
                    && (recordTime) < (endCurrentDay - (timeLineDayInSeconds * it1.toLong()))
                ) {
                    val time = ((startCurrentDay - (timeLineDayInSeconds * it1.toLong())) / 1000).toInt()
                    newPushUps.add(PushUpSum(it2.sumCountPushUps, time))
                    pushUps.remove(it2)
                }
            }
            val time = ((startCurrentDay - (timeLineDayInSeconds * it1.toLong())) / 1000).toInt()
            if (newPushUps.none { it.recordTime == time })
                newPushUps.add(PushUpSum(0, time))
        }

        newPushUps.sortBy { it.recordTime }


        val preLastDayData = newPushUps[newPushUps.size - 2].sumCountPushUps
        val lastDayData = newPushUps[newPushUps.size - 1].sumCountPushUps
        setStatisticDataDaily(preLastDayData, lastDayData)

        val entries = newPushUps.map { pushUp ->
            val date = Date(pushUp.recordTime.toLong() * 1000)
            Entry((date.time / 864).toFloat() + 6000f, pushUp.sumCountPushUps.toFloat())
        }

        // create a dataset and customize its appearance
        val dataSet = LineDataSet(entries, "Push-Up Count")
        dataSet.color = Color.BLUE
        dataSet.lineWidth = 4.0f
        dataSet.setDrawValues(true)
        dataSet.circleRadius = 4.0f

        // create a LineData object with the dataset
        val lineData = LineData(dataSet)

        // set the data and customize the appearance of the chart
        lineChart.data = lineData

        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.setPinchZoom(true)
        lineChart.isDoubleTapToZoomEnabled = false

        // enable horizontal scrolling
        lineChart.isDragEnabled = true

        // customize the appearance of the X-axis
        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return formatStartMonthAndDay.format(Date(value.toLong() * 864))
            }
        }
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        //xAxis.granularity = 86400000f// - 86400000f  // one day in milliseconds
        xAxis.granularity = 10f//
        xAxis.labelCount = 7
        
        // set the visible range of the X-axis
        lineChart.setVisibleXRangeMaximum(7 * 100000f) // 7 days
        lineChart.moveViewToX(entries.last().x) // scroll to the end of the chart

    }

    private fun setStatisticDataDaily(preLastDayData: Int, lastDayData: Int) {
        //val dateFormatDay = formatFullDateWithDots
        dailyStatistics.line1 = "Dzień: " + formatFullDateWithDots.format(Date().time)
        val com = if (preLastDayData < lastDayData) "% więcej " else "% mniej "

        var procent = (abs(preLastDayData - lastDayData).toFloat() / lastDayData.toFloat()) * 100

        procent = if (procent > 100.0f) 100.0f else procent

        if (preLastDayData == lastDayData) {
            dailyStatistics.line2 = "Dziś jest jak wczoraj(" + preLastDayData.toString() + ")"
        } else {
            dailyStatistics.line2 =
                "Dziś(" + lastDayData.toString() + ") na " + procent.roundToLong().toString() +
                        com + "niż poprzedniego dnia (" + preLastDayData.toString() + ")"
        }

        dailyStatistics.line3 =
            "Jeśli tyle razy każdego dnia, to za tydzień ${lastDayData * 7} za miesiąc ${lastDayData * 31} za rok ${lastDayData * 365}"
    }

    private fun setGraphWeekly(pushUpSums: MutableList<PushUpSum>) {
        val lineChart: LineChart = binding.lineChartWeekly
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, 2)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 1)
        calendar.set(Calendar.MILLISECOND, 0)
        val startCurrentWeek = calendar.timeInMillis
        val timeLine = 86400000 * 7
        val endCurrentDay = calendar.timeInMillis + timeLine
        val newPushUps = mutableListOf<PushUpSum>()

        //if day cant record set 0 count
        (0..25).forEach { it1 ->
            pushUpSums.forEach { it2 ->
                val recordTime = (it2.recordTime).toLong() * 1000
                if ((startCurrentWeek - (timeLine * it1.toLong())) < (recordTime)
                    && (recordTime) < (endCurrentDay - (timeLine * it1.toLong()))
                ) {
                    val time = ((startCurrentWeek - (timeLine * it1.toLong())) / 1000).toInt()
                    newPushUps.add(PushUpSum(it2.sumCountPushUps, time))
                }
            }
            val time = ((startCurrentWeek - (timeLine * it1.toLong())) / 1000).toInt()
            if (newPushUps.none { it.recordTime == time })
                newPushUps.add(PushUpSum(0, time))
        }

        newPushUps.sortBy { it.recordTime }

        val lastPushUp = newPushUps[newPushUps.size - 1].sumCountPushUps
        setStatisticDataWeekly(lastPushUp)

        val entries = newPushUps.map { pushUp ->
            val date = Date(pushUp.recordTime.toLong() * 1000) // convert Unix timestamp to Date
            Entry(
                (date.time.toFloat() / (864 * 7.toFloat())) + 45000f,
                pushUp.sumCountPushUps.toFloat()
            )
        }


        // create a dataset and customize its appearance
        val dataSet = LineDataSet(entries, "Push-Up Count")
        dataSet.color = Color.BLUE
        dataSet.lineWidth = 4.0f
        dataSet.setDrawValues(true)
        dataSet.circleRadius = 4.0f

        // create a LineData object with the dataset
        val lineData = LineData(dataSet)

        // set the data and customize the appearance of the chart
        lineChart.data = lineData

        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.setPinchZoom(true)
        lineChart.isDoubleTapToZoomEnabled = false

        // enable horizontal scrolling
        lineChart.isDragEnabled = true

        // customize the appearance of the X-axis
        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return formatStartMonthAndNumberMonthWeek.format(Date(value.toLong() * 864 * 7))
            }
        }
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 100000f
        xAxis.labelCount = 7


        // set the visible range of the X-axis
        lineChart.setVisibleXRangeMaximum(7 * 100000f) // 7 days
        lineChart.moveViewToX(entries.last().x) // scroll to the end of the chart
    }

    private fun setStatisticDataWeekly(lastPushUp: Int) {
        val calendar2 = Calendar.getInstance()
        calendar2.time = Date()
        calendar2.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val calendar3 = Calendar.getInstance()
        calendar3.time = Date()
        calendar3.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        weeklyStatistics.line1 =
            "Tydzień " + formatFullDateWithDots.format(calendar2.time) + " - " +
                    formatFullDateWithDots.format(calendar3.time)
        weeklyStatistics.line2 = "Suma za tydzień(zaczyna się w poniedziałek): $lastPushUp"
        val diffInMillies = calendar2.time.time - Date().time
        val dayBetweenTwoDates = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)
        weeklyStatistics.line3 =
            "Średnia liczba na dzień tygodnia: ${(lastPushUp.toFloat() / (abs(dayBetweenTwoDates) + 1).toFloat()).roundToLong()}"
    }

    private fun setGraphMonthly(pushUpSums: MutableList<PushUpSumMonth>) {

        val lineChart: LineChart = binding.lineChartMonthly

        val entries = pushUpSums.map { pushUp ->
            val date = formatYearAndMonth.parse(pushUp.monthNumber)
            val timestampMillis = date?.time ?: 0
            Entry((timestampMillis / 2678400000).toFloat() + 1f, pushUp.sumCountPushUps.toFloat())
        }

        val pushUpSum =  pushUpSums[pushUpSums.size-1].sumCountPushUps

        setStatisticDataMonthly(pushUpSum)

        // create a dataset and customize its appearance
        val dataSet = LineDataSet(entries, "Push-Up Count")
        dataSet.color = Color.BLUE
        dataSet.lineWidth = 4.0f
        dataSet.setDrawValues(true)
        dataSet.circleRadius = 4.0f

        // create a LineData object with the dataset
        val lineData = LineData(dataSet)

        // set the data and customize the appearance of the chart
        lineChart.data = lineData

        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.setPinchZoom(true)
        lineChart.isDoubleTapToZoomEnabled = false

        // enable horizontal scrolling
        lineChart.isDragEnabled = true

        // customize the appearance of the X-axis
        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return formatStartMonthAndYear.format(Date(value.toLong() * 2678400000))
            }
        }
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.labelCount = 5

        // set the visible range of the X-axis
        lineChart.setVisibleXRangeMaximum(5f) // 7 days
        lineChart.moveViewToX(entries.last().x) // scroll to the end of the chart
    }

    private fun setStatisticDataMonthly(pushUpSum: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = calendar.time
        val startDateStr = formatFullDateWithDots.format(startDate)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val endDate = calendar.time
        val endDateStr = formatFullDateWithDots.format(endDate)
        monthlyStatistics.line1 =
            "Obecny miesiąc: ${formatFullNameMonth.format(Calendar.getInstance().time)}" +
                    " $startDateStr - $endDateStr"
        monthlyStatistics.line2 = "Ilość pompek na ten miesiąc: ${pushUpSum}"

        val calendar2 = Calendar.getInstance()
        val currentDayOfMonth = calendar2.get(Calendar.DAY_OF_MONTH)
        calendar2.set(Calendar.DAY_OF_MONTH, 1)

        val averageNumber = (pushUpSum.toFloat() / currentDayOfMonth.toFloat()).roundToLong()
        monthlyStatistics.line3 = "Średnia liczba na dzień miesiąca: $averageNumber"
    }


    private fun setGraphYearly(pushUpSumYears: MutableList<PushUpSumYear>) {

        val lineChart: LineChart = binding.lineChartYearly

        val entries = pushUpSumYears.map { pushUp ->
            Entry(pushUp.year.toFloat(), pushUp.totalPushUps.toFloat())
        }

        val sumPushUpSumYear = pushUpSumYears[pushUpSumYears.size-1].totalPushUps
        setStatisticDataYearly(sumPushUpSumYear)

        // create a dataset and customize its appearance
        val dataSet = LineDataSet(entries, "Push-Up Count") // todo replace in all pleases
        dataSet.color = Color.BLUE
        dataSet.lineWidth = 4.0f
        dataSet.setDrawValues(true)
        dataSet.circleRadius = 4.0f

        // create a LineData object with the dataset
        val lineData = LineData(dataSet)

        // set the data and customize the appearance of the chart
        lineChart.data = lineData

        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.setPinchZoom(true)
        lineChart.isDoubleTapToZoomEnabled = false

        // enable horizontal scrolling
        lineChart.isDragEnabled = true

        // customize the appearance of the X-axis
        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() + " y"
            }
        }
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.labelCount = 5


        // set the visible range of the X-axis
        lineChart.setVisibleXRangeMaximum(5f) // 7 days
        lineChart.moveViewToX(entries.last().x) // scroll to the end of the chart

    }

    private fun setStatisticDataYearly(sumPushUpSumYear: Int) {
        yearlyStatistics.line1 = "Rok: ${formatYear.format(Date())}"
        yearlyStatistics.line2 = "Ilość pompek na ten rok: $sumPushUpSumYear"
        val dayStartYearAndNow = formatDayInYear.format(Date()).toInt()
        val averagePushUpInYear =
            (sumPushUpSumYear.toFloat() / dayStartYearAndNow.toFloat()).roundToLong()
        yearlyStatistics.line3 = "Średnia liczba na dzień roku: $averagePushUpInYear"
    }

    private fun initGraphData() {
        when (periodOptions) {
            PeriodOptions.DAILY -> {
                statisticsViewModel.getDailyData()
            }
            PeriodOptions.WEEKLY -> {
                statisticsViewModel.getWeeklyData()
            }
            PeriodOptions.MONTHLY -> {
                statisticsViewModel.getMonthlyData()
            }
            PeriodOptions.YEARLY -> {
                statisticsViewModel.getYearlyData()
            }
        }

    }

    private fun initFilterAfterResume() {
        //init color
        binding.timePeriodOptionsLayout.forEach {
            if (it is TextView) {
                it.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.white)
                )
            }
        }
        binding.dailyOptionTextView.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.purple_500
            )
        )

    }

    private fun optionsSelected(id: Int) {
        when (id) {
            binding.dailyOptionTextView.id -> {
                periodOptions = PeriodOptions.DAILY
            }
            binding.weeklyOptionTextView.id -> {
                periodOptions = PeriodOptions.WEEKLY
            }
            binding.monthlyOptionTextView.id -> {
                periodOptions = PeriodOptions.MONTHLY
            }
            binding.yearlyOptionTextView.id -> {
                periodOptions = PeriodOptions.YEARLY
            }
        }
        setVisibilityGraphs()
        initGraphData()
        initStatistics()
    }

    private fun initStatistics() {
        when (periodOptions) {
            PeriodOptions.DAILY -> {
                updateStatisticsDetails(dailyStatistics)
            }
            PeriodOptions.WEEKLY -> {
                statisticsViewModel.getWeeklyData()
                updateStatisticsDetails(weeklyStatistics)
            }
            PeriodOptions.MONTHLY -> {
                statisticsViewModel.getMonthlyData()
                updateStatisticsDetails(monthlyStatistics)
            }
            PeriodOptions.YEARLY -> {
                statisticsViewModel.getYearlyData()
                updateStatisticsDetails(yearlyStatistics)
            }
        }
    }

    private fun setVisibilityGraphs() {
        val lineChartDaily: LineChart = binding.lineChartDaily
        val lineChartWeekly: LineChart = binding.lineChartWeekly
        val lineChartMonthly: LineChart = binding.lineChartMonthly
        val lineChartYearly: LineChart = binding.lineChartYearly
        lineChartDaily.visibility = View.GONE
        lineChartWeekly.visibility = View.GONE
        lineChartMonthly.visibility = View.GONE
        lineChartYearly.visibility = View.GONE
        when (periodOptions) {
            PeriodOptions.DAILY -> {
                lineChartDaily.visibility = View.VISIBLE
            }
            PeriodOptions.WEEKLY -> {
                lineChartWeekly.visibility = View.VISIBLE
            }
            PeriodOptions.MONTHLY -> {
                lineChartMonthly.visibility = View.VISIBLE
            }
            PeriodOptions.YEARLY -> {
                lineChartYearly.visibility = View.VISIBLE
            }
        }
    }

    private fun optionsLayoutElementsColorSelected() {
        binding.timePeriodOptionsLayout.forEach { it ->
            it.setOnClickListener {
                if (it is TextView) {
                    //start fun after click in filter
                    optionsSelected(it.id)
                    it.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
                    binding.timePeriodOptionsLayout.forEach { it2 ->
                        if (it2 is TextView) {
                            if (it.id != it2.id)
                                it2.setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.white)
                                )
                        }
                    }
                }
            }
        }
    }


}