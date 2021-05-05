package com.thing.bangkit.soulmood

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.thing.bangkit.soulmood.adapter.GroupNameViewAdapter
import com.thing.bangkit.soulmood.databinding.ActivityAcitivtyUntukCobaCobaBinding
import com.thing.bangkit.soulmood.viewmodel.GroupNameViewModel

class AcitivtyUntukCobaCoba : AppCompatActivity() {
    private val groupNameViewModel:GroupNameViewModel by viewModels()
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
                            groupNameViewModel.insertNewGroup(groupName,this@AcitivtyUntukCobaCoba)
                        }
                    }
                }
            }

            groupNameViewModel.setGroupName()
            groupNameViewModel.getGroupName().observe(this@AcitivtyUntukCobaCoba, {
                if (it != null) {
                    adapter.setData(it)
                }
            })
        }
    }
}