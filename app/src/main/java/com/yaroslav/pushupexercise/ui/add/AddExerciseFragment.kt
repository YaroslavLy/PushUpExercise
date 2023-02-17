package com.yaroslav.pushupexercise.ui.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.yaroslav.pushupexercise.R
import com.yaroslav.pushupexercise.appComponent
import com.yaroslav.pushupexercise.databinding.FragmentAddExerciseBinding
import com.yaroslav.pushupexercise.databinding.FragmentMainBinding
import com.yaroslav.pushupexercise.ui.main.MainViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddExerciseFragment : Fragment() {

    private var _binding: FragmentAddExerciseBinding? = null
    private val binding get() = _binding!!

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
        addPushUpViewModel.getData(args.idPushUp)
        lifecycleScope.launch {
            addPushUpViewModel.countPushUpsState.collect{
                binding.count.setText(it.toString())
            }
        }

        lifecycleScope.launch {
            addPushUpViewModel.timeState.collect{
                //todo move to utils
                val sdf = SimpleDateFormat( "hh:mm")
                binding.buttonTime.text = sdf.format(Date(it.toLong()*1000)).toString()
            }
        }

    }

}