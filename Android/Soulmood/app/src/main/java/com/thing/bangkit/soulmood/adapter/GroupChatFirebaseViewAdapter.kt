package com.thing.bangkit.soulmood.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.thing.bangkit.soulmood.databinding.ItemChatBinding
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.helper.SharedPref
import com.thing.bangkit.soulmood.model.ChatMessage

class GroupChatFirebaseViewAdapter(options:FirestoreRecyclerOptions<ChatMessage>) :
    FirestoreRecyclerAdapter<ChatMessage, GroupChatFirebaseViewAdapter.ViewHolder>(options) {

    class ViewHolder(val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
        model: ChatMessage
    ) {
        holder.binding.apply {

            //jika pesan dari pengirim
            if(model.email == SharedPref.getPref(holder.itemView.context, MyAsset.KEY_EMAIL)){
                llReceiver.visibility = View.GONE
                llSender.visibility = View.VISIBLE
                tvSenderName.text = model.sender
                tvMessageSender.text = model.ai_message
                tvDateSender.text = model.created_at
            }else{
                llSender.visibility = View.GONE
                llReceiver.visibility = View.VISIBLE
                tvReceiverName.text = model.sender
                tvMessageReceiver.text = model.ai_message
                tvDateReceiver.text = model.created_at
            }
            if(model.status =="true"){
                tvMessageSender.setTextColor(Color.RED)
                tvMessageReceiver.setTextColor(Color.RED)
            }else{
                tvMessageSender.setTextColor(Color.BLACK)
                tvMessageReceiver.setTextColor(Color.BLACK)
            }

        }
    }

}