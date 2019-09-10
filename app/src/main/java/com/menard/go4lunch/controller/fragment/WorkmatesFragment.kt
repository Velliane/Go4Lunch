package com.menard.go4lunch.controller.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.menard.go4lunch.R
import com.menard.go4lunch.adapter.ListViewAdapter
import com.menard.go4lunch.adapter.WorkmatesAdapter
import com.menard.go4lunch.api.UserHelper
import com.menard.go4lunch.model.User

class WorkmatesFragment: BaseFragment(){

    /** RecyclerView */
    private lateinit var recyclerView: RecyclerView

    companion object{
        fun newInstance(): WorkmatesFragment{
            return WorkmatesFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.workmates_fragment, container, false)

        recyclerView = view.findViewById(R.id.list_workmates)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.HORIZONTAL))

        val query: Query = UserHelper.getAllUser()
        val list = FirestoreRecyclerOptions.Builder<User>().setQuery(query, User::class.java)
                .setLifecycleOwner(this).build()

        //-- Layout manager --
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        val workmatesAdapter = WorkmatesAdapter(requireActivity(),list )
        recyclerView.adapter = workmatesAdapter

        return view
    }
}