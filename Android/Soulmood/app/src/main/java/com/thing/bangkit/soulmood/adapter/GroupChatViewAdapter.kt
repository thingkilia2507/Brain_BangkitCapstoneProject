package com.thing.bangkit.soulmood.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thing.bangkit.soulmood.databinding.ItemChatBinding
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.helper.SharedPref
import com.thing.bangkit.soulmood.model.ChatMessage

class GroupChatViewAdapter : RecyclerView.Adapter<GroupChatViewAdapter.ViewHolder>() {
    private val data = ArrayList<ChatMessage>()

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            //jika pesan dari pengirim
            if(data[position].email == SharedPref.getPref(holder.itemView.context, MyAsset.KEY_EMAIL)){
                llReceiver.visibility = View.GONE
                llSender.visibility = View.VISIBLE
                tvSenderName.text = data[position].sender
                tvMessageSender.text = data[position].ai_message
                tvDateSender.text = data[position].created_at
            }else{
                llSender.visibility = View.GONE
                llReceiver.visibility = View.VISIBLE
                tvReceiverName.text = data[position].sender
                tvMessageReceiver.text = data[position].ai_message
                tvDateReceiver.text = data[position].created_at
            }
            if(data[position].status =="true"){
                tvMessageSender.setTextColor(Color.RED)
                tvMessageReceiver.setTextColor(Color.RED)
            }else{
                tvMessageSender.setTextColor(Color.BLACK)
                tvMessageReceiver.setTextColor(Color.BLACK)
            }

        }
    }

    override fun getItemCount(): Int = data.size


    fun setData(data: ArrayList<ChatMessage>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }


}