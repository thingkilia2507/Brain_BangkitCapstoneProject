package com.thing.bangkit.soulmood

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.thing.bangkit.soulmood.adapter.GroupNameViewAdapter
import com.thing.bangkit.soulmood.databinding.ActivityAcitivtyUntukCobaCobaBinding
import com.thing.bangkit.soulmood.model.ChatGroup
import com.thing.bangkit.soulmood.viewmodel.GroupChatViewModel

class AcitivtyUntukCobaCoba : AppCompatActivity() {
    private val groupChatViewModel:GroupChatViewModel by viewModels()
    private lateinit var binding:ActivityAcitivtyUntukCobaCobaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAcitivtyUntukCobaCobaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {
            val adapter = GroupNameViewAdapter()
            rvGroupName.layoutManager = LinearLayoutManager(this@AcitivtyUntukCobaCoba,
                LinearLayoutManager.HORIZONTAL,false)
            rvGroupName.adapter = adapter

            adapter.setOnItemClickCallback(object :GroupNameViewAdapter.OnItemClickCallback{
                override fun onItemClick(data: ChatGroup) {
                    startActivity(Intent(this@AcitivtyUntukCobaCoba, ChatGroupActivity::class.java).apply {
                        putExtra(getString(R.string.group_id),data.id)
                        putExtra(getString(R.string.group_name),data.group_name)
                    })
                }
            })

            floatingAdd.setOnClickListener {
                val dialog= Dialog(this@AcitivtyUntukCobaCoba)
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.add_new_group_dialog)
                dialog.setCancelable(true)
                dialog.show()

                dialog.apply {
                    val etGroupName = findViewById<EditText>(R.id.et_new_group)
                    val btnNewGroup = findViewById<Button>(R.id.btn_add_new_group)
                    btnNewGroup.setOnClickListener {
                        val groupName = etGroupName.text.toString()
                        if(groupName.isEmpty()) etGroupName.error = "Masukkan Nama Grup!"
                        else {
                            groupChatViewModel.insertNewGroup(groupName,this@AcitivtyUntukCobaCoba)
                            dialog.dismiss()
                        }
                    }
                }
            }

            groupChatViewModel.setGroupName()
            groupChatViewModel.getGroupName().observe(this@AcitivtyUntukCobaCoba, {
                if (it != null) {
                    adapter.setData(it)
                }
            })
        }
    }
}