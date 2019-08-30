package com.menard.go4lunch.controller.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.Places
import com.menard.go4lunch.R
import com.menard.go4lunch.adapter.ListViewAdapter

class ListViewFragment: BaseFragment(){


    /** RecyclerView */
    private lateinit var recyclerView: RecyclerView

    companion object{
        fun newInstance():ListViewFragment{
            return ListViewFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.listview_fragment, container, false)

        recyclerView = view.findViewById(R.id.listview_fragment_recycler_view)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.HORIZONTAL))
        //-- Layout manager --
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        val listViewAdapter = ListViewAdapter()
        recyclerView.adapter = listViewAdapter


        return view
    }

}