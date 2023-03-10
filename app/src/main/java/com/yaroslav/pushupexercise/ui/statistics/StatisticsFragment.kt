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
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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

    //private var periodOptions = PeriodOptions.DAILY

    private var dailyStatistics = DetailsStatistics()
    private var weeklyStatistics = DetailsStatistics()
    private var monthlyStatistics = DetailsStatistics()
    private var yearlyStatistics = DetailsStatistics()


    private val statisticsViewModel: StatisticsViewModel by viewModels {
        appComponent.statisticsViewModelsFactory()
    }

    override fun onResume() {
        super.onResume()
        //initFilterAfterResume()//start dailyOptionTextView
        //initGraphData(periodOptions)//start dailyOptionTextView
        initAfterResume(statisticsViewModel.periodOptions)
    }

    private fun initAfterResume(periodOptions: PeriodOptions) {
        initFilterAfterResume(periodOptions)
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
        val args: StatisticsFragmentArgs by navArgs()



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

        lifecycleScope.launch {
            statisticsViewModel.pushUpsSumYearlyState.collect {
                if (it.isNotEmpty()) {
                    setGraphYearly(it.toMutableList())
                    updateStatisticsDetails(yearlyStatistics)
                }

            }
        }

        optionsLayoutElementsColorSelected()

        // Initialize the OnBackPressedCallback
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //super.handleOnBackPressed()
                val action =
                    StatisticsFragmentDirections.actionStatisticsFragmentToMainFragment(data = args.date)
                findNavController().navigate(action)
            }
        }

        // Add the OnBackPressedCallback to the fragment's lifecycle
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

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
                    val time =
                        ((startCurrentDay - (timeLineDayInSeconds * it1.toLong())) / 1000).toInt()
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
        val dataSet = LineDataSet(entries, getString(R.string.push_up_count))
        dataSet.color = ContextCompat.getColor(
            requireContext(),
            R.color.primaryDarkColor
        )//Color.BLUE

        dataSet.lineWidth = 4.0f
        dataSet.setDrawValues(true)
        dataSet.circleRadius = 4.0f
        //todo add all places
        dataSet.valueTextColor = ContextCompat.getColor(
            requireContext(),
            R.color.purple_500
        )
        //dataSet.color

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

        //todo add in all places
        lineChart.xAxis.textColor = ContextCompat.getColor(
            requireContext(),
            R.color.purple_500
        )
        lineChart.axisLeft.textColor = ContextCompat.getColor(
            requireContext(),
            R.color.purple_500
        )
        lineChart.axisRight.textColor = ContextCompat.getColor(
            requireContext(),
            R.color.purple_500
        )


        // customize the appearance of the X-axis
        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return formatStartMonthAndDay.format(Date(value.toLong() * 864))
            }
        }
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 10f//
        xAxis.labelCount = 7

        // set the visible range of the X-axis
        lineChart.setVisibleXRangeMaximum(7 * 100000f) // 7 days
        lineChart.moveViewToX(entries.last().x) // scroll to the end of the chart

    }

    private fun setStatisticDataDaily(preLastDayData: Int, lastDayData: Int) {
        dailyStatistics.line1 =
            getString(R.string.day_with_data, formatFullDateWithDots.format(Date().time))
        val com = if (preLastDayData < lastDayData) getString(R.string.more_procent)
        else
            getString(R.string.less_procent)

        val procent = if (preLastDayData < lastDayData)
            ((lastDayData - preLastDayData).toFloat() / preLastDayData.toFloat()) * 100
        else
            ((preLastDayData - lastDayData).toFloat() / lastDayData.toFloat()) * 100


        //todo not 31 bu days in current month
        dailyStatistics.line3 = getString(
            R.string.prognosis_for_week_month_year,
            lastDayData * 7,
            lastDayData * 31,
            lastDayData * 365
        )

        if(lastDayData==0){
            dailyStatistics.line2 = getString(R.string.no_push_ups)
            return
        }

        if(preLastDayData == 0){
            dailyStatistics.line2 = getString(R.string.no_push_ups_day_before)
            return
        }

        if (preLastDayData == lastDayData) {
            dailyStatistics.line2 =
                getString(R.string.equals_previous_day, preLastDayData.toString())
        } else {
            dailyStatistics.line2 = getString(
                R.string.not_equals_previous_day,
                lastDayData.toString(),
                procent.roundToLong().toString(),
                com,
                preLastDayData.toString()
            )
        }

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
        val dataSet = LineDataSet(entries, getString(R.string.push_up_count))
        dataSet.color = ContextCompat.getColor(
            requireContext(),
            R.color.primaryDarkColor
        )//Color.BLUE
        dataSet.lineWidth = 4.0f
        dataSet.setDrawValues(true)
        dataSet.circleRadius = 4.0f
        dataSet.valueTextColor = ContextCompat.getColor(
            requireContext(),
            R.color.purple_500
        )

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

        lineChart.xAxis.textColor = ContextCompat.getColor(
            requireContext(),
            R.color.purple_500
        )
        lineChart.axisLeft.textColor = ContextCompat.getColor(
            requireContext(),
            R.color.purple_500
        )
        lineChart.axisRight.textColor = ContextCompat.getColor(
            requireContext(),
            R.color.purple_500
        )

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
        weeklyStatistics.line1 = getString(
            R.string.week_start_end,
            formatFullDateWithDots.format(calendar2.time),
            formatFullDateWithDots.format(calendar3.time)
        )
        weeklyStatistics.line2 = getString(R.string.sum_week, lastPushUp)
        val diffInMillies = calendar2.time.time - Date().time
        val dayBetweenTwoDates = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)
        weeklyStatistics.line3 = getString(
            R.string.average_week_push_ups,
            (lastPushUp.toFloat() / (abs(dayBetweenTwoDates) + 1).toFloat()).roundToLong()
        )
    }

    private fun setGraphMonthly(pushUpSums: MutableList<PushUpSumMonth>) {

        val lineChart: LineChart = binding.lineChartMonthly

        val entries = pushUpSums.map { pushUp ->
            val date = formatYearAndMonth.parse(pushUp.monthNumber)
            val timestampMillis = date?.time ?: 0
            Entry((timestampMillis / 2678400000).toFloat() + 1f, pushUp.sumCountPushUps.toFloat())
        }

        val pushUpSum = pushUpSums[pushUpSums.size - 1].sumCountPushUps

        setStatisticDataMonthly(pushUpSum)

        // create a dataset and customize its appearance
        val dataSet = LineDataSet(entries, getString(R.string.push_up_count))
        dataSet.color = ContextCompat.getColor(
            requireContext(),
            R.color.primaryDarkColor
        )//Color.BLUE
        dataSet.lineWidth = 4.0f
        dataSet.setDrawValues(true)
        dataSet.circleRadius = 4.0f
        dataSet.valueTextColor = ContextCompat.getColor(
            requireContext(),
            R.color.purple_500
        )

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

        lineChart.xAxis.textColor = ContextCompat.getColor(
            requireContext(),
            R.color.purple_500
        )
        lineChart.axisLeft.textColor = ContextCompat.getColor(
            requireContext(),
            R.color.purple_500
        )
        lineChart.axisRight.textColor = ContextCompat.getColor(
            requireContext(),
            R.color.purple_500
        )

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
        monthlyStatistics.line1 = getString(
            R.string.month_start_end, formatFullNameMonth.format(Calendar.getInstance().time),
            startDateStr, endDateStr
        )

        monthlyStatistics.line2 = getString(R.string.count_push_ups_month, pushUpSum)

        val calendar2 = Calendar.getInstance()
        val currentDayOfMonth = calendar2.get(Calendar.DAY_OF_MONTH)
        calendar2.set(Calendar.DAY_OF_MONTH, 1)

        val averageNumber = (pushUpSum.toFloat() / currentDayOfMonth.toFloat()).roundToLong()
        monthlyStatistics.line3 = getString(R.string.average_push_ups_in_month, averageNumber)
    }


    private fun setGraphYearly(pushUpSumYears: MutableList<PushUpSumYear>) {

        val lineChart: LineChart = binding.lineChartYearly

        val entries = pushUpSumYears.map { pushUp ->
            Entry(pushUp.year.toFloat(), pushUp.totalPushUps.toFloat())
        }

        val sumPushUpSumYear = pushUpSumYears[pushUpSumYears.size - 1].totalPushUps
        setStatisticDataYearly(sumPushUpSumYear)

        // create a dataset and customize its appearance
        val dataSet = LineDataSet(entries, getString(R.string.push_up_count))
        dataSet.color = ContextCompat.getColor(
            requireContext(),
            R.color.primaryDarkColor
        )//Color.BLUE
        dataSet.lineWidth = 4.0f
        dataSet.setDrawValues(true)
        dataSet.circleRadius = 4.0f
        dataSet.valueTextColor = ContextCompat.getColor(
            requireContext(),
            R.color.purple_500
        )

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

        lineChart.xAxis.textColor = ContextCompat.getColor(
            requireContext(),
            R.color.purple_500
        )
        lineChart.axisLeft.textColor = ContextCompat.getColor(
            requireContext(),
            R.color.purple_500
        )
        lineChart.axisRight.textColor = ContextCompat.getColor(
            requireContext(),
            R.color.purple_500
        )

        // customize the appearance of the X-axis
        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() //+ " y"
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
        yearlyStatistics.line1 = getString(R.string.year_number, formatYear.format(Date()))
        yearlyStatistics.line2 = getString(R.string.count_push_up_year, sumPushUpSumYear)
        val dayStartYearAndNow = formatDayInYear.format(Date()).toInt()
        val averagePushUpInYear =
            (sumPushUpSumYear.toFloat() / dayStartYearAndNow.toFloat()).roundToLong()
        yearlyStatistics.line3 = getString(R.string.average_yearly_push_ups, averagePushUpInYear)
    }

    private fun initGraphData() {
        when (statisticsViewModel.periodOptions) {
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

    private fun initFilterAfterResume(periodOptions: PeriodOptions) {
        //init color
        binding.timePeriodOptionsLayout.forEach {
            if (it is TextView) {
                it.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.white)
                )
            }
        }
        when (periodOptions) {
            PeriodOptions.DAILY -> {
                binding.dailyOptionTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_500
                    )
                )
                statisticsViewModel.getDailyData()
            }
            PeriodOptions.WEEKLY -> {
                binding.weeklyOptionTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_500
                    )
                )
                statisticsViewModel.getWeeklyData()
            }
            PeriodOptions.MONTHLY -> {
                binding.monthlyOptionTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_500
                    )
                )
                statisticsViewModel.getMonthlyData()
            }
            PeriodOptions.YEARLY -> {
                binding.yearlyOptionTextView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.purple_500
                    )
                )
                statisticsViewModel.getYearlyData()
            }
        }
        //statisticsViewModel.setperiodOptions(periodOptions)
        //binding.dailyOptionTextView
        setVisibilityGraphs()
        initGraphData()
        initStatistics()

    }

    private fun optionsSelected(id: Int) {
        when (id) {
            binding.dailyOptionTextView.id -> {
                statisticsViewModel.setperiodOptions(PeriodOptions.DAILY)
            }
            binding.weeklyOptionTextView.id -> {
                statisticsViewModel.setperiodOptions(PeriodOptions.WEEKLY)
            }
            binding.monthlyOptionTextView.id -> {
                statisticsViewModel.setperiodOptions(PeriodOptions.MONTHLY)
            }
            binding.yearlyOptionTextView.id -> {
                statisticsViewModel.setperiodOptions(PeriodOptions.YEARLY)
            }
        }
        setVisibilityGraphs()
        initGraphData()
        initStatistics()
    }

    private fun initStatistics() {
        when (statisticsViewModel.periodOptions) {
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
        when (statisticsViewModel.periodOptions) {
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