package com.thing.bangkit.soulmood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.databinding.ItemSplashBinding
import com.thing.bangkit.soulmood.model.SplashData

class SplashViewAdapter(var data: ArrayList<SplashData>, var viewPager2: ViewPager2):
    RecyclerView.Adapter<SplashViewAdapter.ViewHolder>() {
    class ViewHolder(val binding:ItemSplashBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemSplashBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    }

    override fun onBindViewHolder(holder: SplashViewAdapter.ViewHolder, position: Int) {
        holder.binding.apply {
            tvSplashTitle.text = data[position].title
            tvSplashDesc.text = data[position].description
            ivSplash.setImageResource(data[position].image)
        }
    }

    override fun getItemCount(): Int = data.size

}