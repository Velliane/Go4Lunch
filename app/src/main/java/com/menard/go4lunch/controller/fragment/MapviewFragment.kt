package com.menard.go4lunch.controller.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.menard.go4lunch.R

class MapviewFragment: Fragment(){

    companion object {

        fun newInstance(): MapviewFragment {
            return MapviewFragment()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.mapview_fragment, container, false)

        return view
    }
}