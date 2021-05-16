package com.thing.bangkit.soulmood.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.databinding.ItemGroupNameBinding
import com.thing.bangkit.soulmood.model.ChatGroup
import java.util.*
import kotlin.collections.ArrayList


class GroupNameViewAdapter(private val type:String) : RecyclerView.Adapter<GroupNameViewAdapter.ViewHolder>() {
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            if(type == "homeFragment"){
                cvGroupName.visibility = View.GONE
                llGroupName.visibility= View.VISIBLE
            val colorGenerator = ColorGenerator.MATERIAL
            val textDrawable = TextDrawable.builder().beginConfig().width(50)
                .height(50).endConfig()
                .buildRound(
                    getInitialName(data[position].group_name.toUpperCase(Locale.getDefault())),
                    colorGenerator.getColor(data[position].group_name)
                )
            ivGroupName.setImageDrawable(textDrawable)
            tvGroupName.text = data[position].group_name
            llGroupName.setOnClickListener {
                   onItemClickCallback.onItemClick(data[position])
            }
        }else{

//                val colors =
//                    intArrayOf(R.drawable.gradient_background, R.drawable.gradient_background, R.drawable.gradient_background)
//                val ranndom = Random()
//                val ranndomColor = ranndom.nextInt(3)
//                cvGroupName.setBackgroundColor(colors[ranndomColor])

                llGroupName.visibility= View.GONE
                cvGroupName.visibility = View.VISIBLE
                tvGroupNameCard.text = data[position].group_name
                cvGroupName.setOnClickListener {
                    onItemClickCallback.onItemClick(data[position])
                }
            }
        }

    }



    override fun getItemCount(): Int {
        return if(data.size >30) 30
        else if(type != "homeFragment") data.size
        else data.size
    }

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