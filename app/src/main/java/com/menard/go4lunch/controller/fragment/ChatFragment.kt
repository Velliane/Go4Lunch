package com.menard.go4lunch.controller.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.menard.go4lunch.R
import com.menard.go4lunch.adapter.ChatAdapter
import com.menard.go4lunch.api.ChatHelper
import com.menard.go4lunch.model.Message

class ChatFragment: BaseFragment() {

    /** RecyclerView */
    private lateinit var recyclerView: RecyclerView

    companion object {
        fun newInstance(): ChatFragment {
            return ChatFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.chat_fragment, container, false)

        recyclerView = view.findViewById(R.id.chat_fragment_recycler_view)

        val query: Query = ChatHelper.getAllMessageForChat()
        val list = FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message::class.java)
                .setLifecycleOwner(this).build()

        //-- Layout manager --
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        val workmatesAdapter = ChatAdapter(requireActivity(),list)
        recyclerView.adapter = workmatesAdapter

        return  view
    }
}