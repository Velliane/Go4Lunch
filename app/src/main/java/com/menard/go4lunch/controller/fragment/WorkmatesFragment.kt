package com.menard.go4lunch.controller.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.menard.go4lunch.R

class WorkmatesFragment: BaseFragment(){

    companion object{
        fun newInstance(): WorkmatesFragment{
            return WorkmatesFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.workmates_fragment, container, false)

        return view
    }
}