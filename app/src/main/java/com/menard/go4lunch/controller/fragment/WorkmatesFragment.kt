package com.menard.go4lunch.controller.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.menard.go4lunch.BuildConfig
import com.menard.go4lunch.R

class WorkmatesFragment: Fragment(){

    private lateinit var text:TextView

    private val API_KEY = BuildConfig.api_key_google

    companion object{
        fun newInstance(): WorkmatesFragment{
            return WorkmatesFragment()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.workmates_fragment, container, false)

        text = view.findViewById(R.id.textview)
        text.text = API_KEY
        return view
    }
}