package com.thing.bangkit.soulmood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.thing.bangkit.soulmood.databinding.ItemChatbotBinding
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.helper.SharedPref
import com.thing.bangkit.soulmood.model.ChatbotMessage

class ChatbotFirebaseAdapter(options: FirestoreRecyclerOptions<ChatbotMessage>) :
    FirestoreRecyclerAdapter<ChatbotMessage, ChatbotFirebaseAdapter.ViewHolder>(options)  {
    
    class ViewHolder(val binding: ItemChatbotBinding): RecyclerView.ViewHolder(binding.root) 

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemChatbotBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
        model: ChatbotMessage
    ) {
        holder.binding.apply {
            //jika pesan dari soulmoo
            if(model.email.equals(SharedPref.getPref(holder.itemView.context, MyAsset.KEY_EMAIL),true)){
                llSoulmoo.visibility = View.GONE
                llUserChatbot.visibility = View.VISIBLE
                tvMessageUserChatbot.text = model.message
                tvDateUserChatbot.text = model.created_at.substring(11, 16)
            }else{
                llUserChatbot.visibility = View.GONE
                llSoulmoo.visibility = View.VISIBLE
                tvMessageSoulmoo.text = model.message
                tvDateSoulmoo.text = model.created_at.substring(11, 16)
            }

        }
    }
}