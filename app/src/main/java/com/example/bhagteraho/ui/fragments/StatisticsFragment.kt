package com.example.bhagteraho.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.bhagteraho.R
import com.example.bhagteraho.ui.viewmodels.StatistcsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {
    private val viewModel: StatistcsViewModel by viewModels()
}
