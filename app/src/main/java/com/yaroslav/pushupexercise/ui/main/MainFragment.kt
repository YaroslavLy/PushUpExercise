package com.yaroslav.pushupexercise.ui.main

import android.app.DatePickerDialog
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.yaroslav.pushupexercise.R
import com.yaroslav.pushupexercise.appComponent
import com.yaroslav.pushupexercise.databinding.FragmentMainBinding
import com.yaroslav.pushupexercise.ui.add.AddExerciseFragmentArgs
import com.yaroslav.pushupexercise.utils.formatFullDateWithDots
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class MainFragment : Fragment() {

    private lateinit var adapter: PushUpAdapter

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var dateSeconds = 0

    private val pushUpViewModel: MainViewModel by viewModels {
        appComponent.viewModelsFactory()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onResume() {
        super.onResume()

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: MainFragmentArgs by navArgs()

        //if main fragment launch after navigate is launched with the data that was before navigation
        //else launch with now date
        if(args.data > 0){
            pushUpViewModel.updateDate(args.data,action = 0)
        }else{
            pushUpViewModel.updateDate((Date().time/1000).toInt(),action = 0)
        }

        //different type layoutManager of recyclerView lashes for different orientation
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                // landscape orientation
                binding.recyclerExercise.layoutManager = GridLayoutManager(context,6)
                    //LinearLayoutManager(context)
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                // portrait orientation
                binding.recyclerExercise.layoutManager =
                    LinearLayoutManager(context)
            }
            Configuration.ORIENTATION_UNDEFINED -> {
                // undefined orientation
            }
        }

        adapter = PushUpAdapter(childFragmentManager, this::deletePushUpById, this::editPushUpById)

        binding.recyclerExercise.adapter = adapter

        lifecycleScope.launch {
            pushUpViewModel.pushUpsState.collect() {
                adapter.submitList(it)
            }
        }

        lifecycleScope.launch {
            pushUpViewModel.pushUpsCountStateFlow.collect() {
                binding.textSum.text = getString(R.string.total_push_ups_day_main, it.toString())
            }
        }
        binding.buttonAdd.setOnClickListener {

            val action = MainFragmentDirections
                    .actionMainFragmentToAddExerciseFragment(dateSeconds = dateSeconds)

            findNavController().navigate(action)
        }

        lifecycleScope.launch {
            pushUpViewModel.dateState.collect {
                val date = Date(it.toLong() * 1000)
                dateSeconds = it
                binding.date.text = formatFullDateWithDots.format(date).toString()
            }
        }

        binding.buttonRight.setOnClickListener {
            pushUpViewModel.updateDate(0,action = 1)
        }

        binding.buttonLeft.setOnClickListener {
            pushUpViewModel.updateDate(0,action = -1)
        }

        binding.date.setOnClickListener {
            datePicked()
        }
        binding.buttonRight.setOnLongClickListener {
            pushUpViewModel.updateDate((Date().time/1000).toInt(),action = 0)
            true
        }

        binding.buttonStatistics.setOnClickListener {
        val action = MainFragmentDirections.actionMainFragmentToStatisticsFragment(date = dateSeconds)
        findNavController().navigate(action)
        // findNavController().navigate(R.id.action_mainFragment_to_statisticsFragment)
        }
    }

    private fun datePicked() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Do something with the selected date (e.g. update a TextView with the selected date)
                val calendar = Calendar.getInstance()
                calendar.set(selectedYear, selectedMonth, selectedDay)
                pushUpViewModel.updateDate((calendar.time.time / 1000).toInt(), 0)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun deletePushUpById(id: Int) {
        pushUpViewModel.deletePushUpById(id)
    }

    private fun editPushUpById(id: Int){
        val action = MainFragmentDirections.actionMainFragmentToAddExerciseFragment(id,dateSeconds)
        findNavController().navigate(action)
    }

}