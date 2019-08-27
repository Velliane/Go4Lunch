package com.menard.go4lunch.controller.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.libraries.places.api.Places
import com.menard.go4lunch.R

class ListViewFragment: Fragment(){


    companion object{
        fun newInstance():ListViewFragment{
            return ListViewFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.listview_fragment, container, false)


        return view
    }

}