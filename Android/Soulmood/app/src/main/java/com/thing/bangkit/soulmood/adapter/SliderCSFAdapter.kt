package com.thing.bangkit.soulmood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thing.bangkit.soulmood.databinding.ItemComingSoonFeatureBinding
import com.thing.bangkit.soulmood.model.ComingSoonFeatureSliderItem

class SliderCSFAdapter internal constructor(var listComingSoonFeature : ArrayList<ComingSoonFeatureSliderItem>) :
    RecyclerView.Adapter<SliderCSFAdapter.SliderViewHolder>() {

    private lateinit var binding: ItemComingSoonFeatureBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        binding = ItemComingSoonFeatureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.bind(listComingSoonFeature[position].image)
    }

    override fun getItemCount(): Int = listComingSoonFeature.size

    class SliderViewHolder(val binding: ItemComingSoonFeatureBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Int) {
            binding.ivComingSoonFeature.setImageResource(image)
        }
    }

}