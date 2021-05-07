package com.thing.bangkit.soulmood.adapter

import android.graphics.Movie
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.thing.bangkit.soulmood.databinding.ItemGroupNameBinding
import com.thing.bangkit.soulmood.model.ChatGroup

class GroupNameViewAdapter : RecyclerView.Adapter<GroupNameViewAdapter.ViewHolder>() {
    private val data = ArrayList<ChatGroup>()
    private lateinit var onItemClickCallback: OnItemClickCallback
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ViewHolder(val binding: ItemGroupNameBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemGroupNameBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GroupNameViewAdapter.ViewHolder, position: Int) {
        holder.binding.apply {
            val colorGenerator = ColorGenerator.MATERIAL
            val textDrawable = TextDrawable.builder().beginConfig().width(50)
                .height(50).endConfig()
                .buildRound(
                    getInitialName(data[position].group_name.toUpperCase()),
                    colorGenerator.getColor(data[position].group_name)
                )
            ivGroupName.setImageDrawable(textDrawable)
            tvGroupName.text = data[position].group_name
            llGroupName.setOnClickListener {
                   onItemClickCallback.onItemClick(data[position])
            }
        }
    }

    override fun getItemCount(): Int = data.size

    interface OnItemClickCallback {
        fun onItemClick(data: ChatGroup)
    }
    fun setData(data: ArrayList<ChatGroup>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }
    private fun getInitialName(name: String): String {
        val splitName = name.split("\\s+")
        val splitCount = splitName.toString().length
        return if (splitCount == 1) {
            " " + name[0] + name[0]
        } else {
            val firstName = name.substring(0, 1)
            val lastSpace = name.lastIndexOf(" ")
            val lastName = name.substring(lastSpace + 1)
            " " + firstName[0] + lastName[0]
        }

    }
}