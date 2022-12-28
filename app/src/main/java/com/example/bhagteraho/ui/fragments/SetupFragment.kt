package com.example.bhagteraho.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bhagteraho.R

class SetupFragment : Fragment(R.layout.fragment_setup) {

    lateinit var text : TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val view = layoutInflater.inflate(R.layout.fragment_setup,null,false)
        text = view.findViewById(R.id.tvContinue)

        text.setOnClickListener{
            findNavController().navigate(R.id.action_setupFragment_to_runFragment)
        }

    }
}