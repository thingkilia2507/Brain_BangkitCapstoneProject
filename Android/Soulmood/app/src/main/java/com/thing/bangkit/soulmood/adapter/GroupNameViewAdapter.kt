package com.thing.bangkit.soulmood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.thing.bangkit.soulmood.databinding.ItemGroupNameBinding
import com.thing.bangkit.soulmood.model.ChatGroup
import java.util.*
import kotlin.collections.ArrayList


class GroupNameViewAdapter(private val type:String) : RecyclerView.Adapter<GroupNameViewAdapter.ViewHolder>() {
    private var data = ArrayList<ChatGroup>()
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val colorGenerator = ColorGenerator.MATERIAL
        holder.binding.apply {
            if(type == "homeFragment"){
                cvGroupName.visibility = View.GONE
                llGroupName.visibility= View.VISIBLE

            ivGroupName.text = getInitialName(data[position].group_name.toUpperCase(Locale("in", "ID")))
                cvGroupName1.setCardBackgroundColor(colorGenerator.randomColor)
                tvGroupName.text = data[position].group_name
            llGroupName.setOnClickListener {
                   onItemClickCallback.onItemClick(data[position])
            }
        }else{
                cvGroupName.setCardBackgroundColor(colorGenerator.randomColor)
                llGroupName.visibility= View.GONE
                cvGroupName.visibility = View.VISIBLE
                tvGroupNameCard.text = data[position].group_name
                cvGroupName.setOnClickListener {
                    onItemClickCallback.onItemClick(data[position])
                }
            }
        }

    }

    override fun getItemCount(): Int =  data.size


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

    fun searchGroupName(groupNameData: ArrayList<ChatGroup>) {
        data = groupNameData
        notifyDataSetChanged()
    }
}