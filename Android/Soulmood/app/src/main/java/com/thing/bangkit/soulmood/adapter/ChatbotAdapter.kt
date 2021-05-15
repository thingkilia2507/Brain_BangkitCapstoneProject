package com.thing.bangkit.soulmood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thing.bangkit.soulmood.databinding.ItemChatbotBinding
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.helper.SharedPref
import com.thing.bangkit.soulmood.model.ChatbotMessage

class ChatbotAdapter : RecyclerView.Adapter<ChatbotAdapter.ViewHolder>() {
    private val data = ArrayList<ChatbotMessage>()


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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            //jika pesan dari soulmoo
            if(data[position].email.equals(SharedPref.getPref(holder.itemView.context, MyAsset.KEY_EMAIL),true)){
                llSoulmoo.visibility = View.GONE
                llUserChatbot.visibility = View.VISIBLE
                tvMessageUserChatbot.text = data[position].message
                tvDateUserChatbot.text = data[position].created_at.substring(11, 16)
            }else{
                llUserChatbot.visibility = View.GONE
                llSoulmoo.visibility = View.VISIBLE
                tvMessageSoulmoo.text = data[position].message
                tvDateSoulmoo.text = data[position].created_at.substring(11, 16)
            }

        }
    }

    override fun getItemCount(): Int = data.size


    fun setData(data: ArrayList<ChatbotMessage>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }


    class ViewHolder(val binding: ItemChatbotBinding) : RecyclerView.ViewHolder(binding.root)
}