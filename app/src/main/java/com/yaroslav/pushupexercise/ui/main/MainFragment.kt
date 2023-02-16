package com.yaroslav.pushupexercise.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.yaroslav.pushupexercise.appComponent
import com.yaroslav.pushupexercise.databinding.FragmentMainBinding
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private lateinit var adapter: PushUpAdapter

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!


    private val pushUpViewModel: MainViewModel by viewModels {
        appComponent.viewModelsFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        //getAppComponent().inject(this)
        return binding.root
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

    }

}