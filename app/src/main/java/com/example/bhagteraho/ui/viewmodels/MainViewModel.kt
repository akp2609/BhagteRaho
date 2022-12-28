package com.example.bhagteraho.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.bhagteraho.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    mainRepository: MainRepository
) : ViewModel(){
}