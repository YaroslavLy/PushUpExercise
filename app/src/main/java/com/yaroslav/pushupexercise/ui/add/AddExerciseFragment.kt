package com.yaroslav.pushupexercise.ui.add

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yaroslav.pushupexercise.R
import com.yaroslav.pushupexercise.appComponent
import com.yaroslav.pushupexercise.databinding.FragmentAddExerciseBinding
import com.yaroslav.pushupexercise.ui.statistics.StatisticsFragmentDirections
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddExerciseFragment : Fragment() {

    private var _binding: FragmentAddExerciseBinding? = null
    private val binding get() = _binding!!

    private var data = 0

    private val addPushUpViewModel: AddViewModel by viewModels {
        appComponent.addViewModelsFactory()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddExerciseBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: AddExerciseFragmentArgs by navArgs()
        addPushUpViewModel.getData(args.idPushUp, args.dateSeconds)

        lifecycleScope.launch {
            addPushUpViewModel.countPushUpsState.collect {
                binding.count.setText(it.toString())
            }
        }

        lifecycleScope.launch {
            addPushUpViewModel.timeState.collect {
                //todo move to utils
                val sdf = SimpleDateFormat("HH:mm")
                binding.buttonTime.text = sdf.format(Date(it.toLong() * 1000)).toString()
            }
        }

        binding.add.setOnClickListener {
            updateCountPushUps()
            addPushUpViewModel.addCount()
        }

        binding.minus.setOnClickListener {
            updateCountPushUps()
            addPushUpViewModel.minusCount()
        }

        binding.buttonCancel.setOnClickListener {
            val action = AddExerciseFragmentDirections
                .actionAddExerciseFragmentToMainFragment(args.dateSeconds)
            findNavController().navigate(action)
        }

        binding.buttonTime.setOnClickListener {
            setTime()
        }

        binding.buttonOk.setOnClickListener {
            addPushUpViewModel.updateCount(binding.count.text.toString().toInt())
            addPushUpViewModel.savePushUp(args.idPushUp)
            val action = AddExerciseFragmentDirections
                .actionAddExerciseFragmentToMainFragment(args.dateSeconds)
            findNavController().navigate(action)
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //super.handleOnBackPressed()
                val action = AddExerciseFragmentDirections
                    .actionAddExerciseFragmentToMainFragment(data = args.dateSeconds)
                findNavController().navigate(action)
            }
        }

        // Add the OnBackPressedCallback to the fragment's lifecycle
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun updateCountPushUps() {
        val editTextCountPushUp = binding.count.text
        if (editTextCountPushUp.isNotEmpty()) {
            addPushUpViewModel.updateCount(editTextCountPushUp.toString().toInt())
        } else {
            addPushUpViewModel.updateCount(0)
        }
    }


    private fun setTime() {
        val currentTime = Calendar.getInstance()
        val hour = currentTime[Calendar.HOUR_OF_DAY]
        val minute = currentTime[Calendar.MINUTE]
        val mTimePicker = TimePickerDialog(
            requireContext(), { timePicker, selectedHour, selectedMinute ->
                //binding.buttonTime.text = "$selectedHour:$selectedMinute"
                //todo move to utils
                val calendar = Calendar.getInstance() // Get a Calendar instance
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                val currentOfDayInMillis = calendar.timeInMillis // Get the time in milliseconds
                val currentOfDayInSecondsRoom = (currentOfDayInMillis / 1000).toInt()
                addPushUpViewModel.updateTime(currentOfDayInSecondsRoom)
            },
            hour,
            minute,
            true
        ) //Yes 24 hour time
        mTimePicker.setTitle("Ustaw Сzas")
        mTimePicker.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0)
    }


}