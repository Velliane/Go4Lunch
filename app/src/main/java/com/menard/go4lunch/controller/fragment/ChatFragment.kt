package com.menard.go4lunch.controller.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.Query
import com.jakewharton.threetenabp.AndroidThreeTen
import com.menard.go4lunch.R
import com.menard.go4lunch.adapter.ChatAdapter
import com.menard.go4lunch.api.ChatHelper
import com.menard.go4lunch.model.Message
import org.threeten.bp.LocalDateTime

class ChatFragment: BaseFragment(), View.OnClickListener {


    /** RecyclerView */
    private lateinit var recyclerView: RecyclerView
    /** Input EditText */
    private lateinit var editMessage: TextInputEditText
    /** Send Button */
    private lateinit var sendButton: Button

    companion object {
        fun newInstance(): ChatFragment {
            return ChatFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.chat_fragment, container, false)

        recyclerView = view.findViewById(R.id.chat_fragment_recycler_view)
        editMessage = view.findViewById(R.id.chat_fragment_edit_text)
        sendButton = view.findViewById(R.id.chat_fragment_send_button)
        sendButton.setOnClickListener(this)

        AndroidThreeTen.init(requireActivity())

        val query: Query = ChatHelper.getAllMessageForChat()
        val list = FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message::class.java)
                .setLifecycleOwner(this).build()

        //-- Recycler View --
        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd
        recyclerView.layoutManager = layoutManager
        val workmatesAdapter = ChatAdapter(requireActivity(),list)
        recyclerView.adapter = workmatesAdapter

        return view
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.chat_fragment_send_button -> {
                val message = editMessage.text.toString()
                if (message != "") {
                    ChatHelper.addMessage(LocalDateTime.now().withNano(0).toString(), editMessage.text.toString(), getCurrentUser().uid)
                    editMessage.setText("")
                }
            }
        }
    }
}