package com.yaroslav.pushupexercise.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.yaroslav.pushupexercise.R
import com.yaroslav.pushupexercise.appComponent
import com.yaroslav.pushupexercise.databinding.FragmentMainBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class MainFragment : Fragment() {

    private lateinit var adapter: PushUpAdapter

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!


    private val pushUpViewModel: MainViewModel by viewModels {
        appComponent.viewModelsFactory()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        //getAppComponent().inject(this)

//        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
//        val currentDate = sdf.format(Date())
//        Log.i("TAG1702",currentDate.toString())
//        val l = Date().time / 1000
//        Log.i("TAG1702",l.toString())
//        //* 1000
//        val ll = Date(l.toLong()* 1000 )
//        val currentDate2 = sdf.format(ll)
//        Log.i("TAG1702",currentDate2.toString())

        //1676627381

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val sdf = SimpleDateFormat("dd/M/yyyy")
        binding.date.text = sdf.format(Date()).toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerExercise.layoutManager =
            LinearLayoutManager(context)

        adapter = PushUpAdapter()

        binding.recyclerExercise.adapter = adapter

        lifecycleScope.launch {
            pushUpViewModel.pushUpsState.collect() {
                adapter.submitList(it)
            }
        }

        lifecycleScope.launch {
            pushUpViewModel.pushUpsCountStateFlow.collect(){
                //todo replace
                binding.textSum.text ="Dzisiaj: "+it.toString()
            }
        }
        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_addExerciseFragment)
        }


//        binding.buttonAdd.setOnClickListener {
//            val mcurrentTime = Calendar.getInstance()
//            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
//            val minute = mcurrentTime[Calendar.MINUTE]
//            val mTimePicker = TimePickerDialog(
//                requireContext(), { timePicker, selectedHour, selectedMinute ->
//                    binding.textSum.setText("$selectedHour:$selectedMinute")
//                },
//                hour,
//                minute,
//                true
//            ) //Yes 24 hour time
//            mTimePicker.setTitle("Select Time")
//            mTimePicker.show()
//        }



    }

}