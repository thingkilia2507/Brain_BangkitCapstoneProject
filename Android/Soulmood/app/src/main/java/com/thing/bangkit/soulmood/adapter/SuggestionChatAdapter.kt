package com.thing.bangkit.soulmood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thing.bangkit.soulmood.databinding.ItemSuggestionChatBinding

class SuggestionChatAdapter(var onSuggestionClicked : () -> Unit) : RecyclerView.Adapter<SuggestionChatAdapter.ViewHolder>() {

    var suggestionMessage = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(ItemSuggestionChatBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvRecommendChat.text = suggestionMessage[position]
        holder.binding.tvRecommendChat.setOnClickListener{ onSuggestionClicked }
    }

    override fun getItemCount(): Int = suggestionMessage.size

    class ViewHolder (var binding : ItemSuggestionChatBinding) : RecyclerView.ViewHolder(binding.root)
}
