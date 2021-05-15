package com.thing.bangkit.soulmood.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.adapter.GroupChatViewAdapter
import com.thing.bangkit.soulmood.databinding.ActivityChatGroupBinding
import com.thing.bangkit.soulmood.viewmodel.GroupChatViewModel

class ChatGroupActivity : AppCompatActivity() {
    private val groupChatViewModel: GroupChatViewModel by viewModels()
    private var binding: ActivityChatGroupBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatGroupBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val groupId = intent.getStringExtra(getString(R.string.group_id))
        val groupName = intent.getStringExtra(getString(R.string.group_name))
        supportActionBar?.title = groupName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        groupChatViewModel.setDataChat(groupId.toString())

        binding?.apply {
            val adapter = GroupChatViewAdapter()
            val linearLayout = LinearLayoutManager(this@ChatGroupActivity)
            //auto scroll recyclerview to bottom
            linearLayout.reverseLayout = true
            rvChatGroup.layoutManager = linearLayout
            rvChatGroup.adapter = adapter

            floatingSend.setOnClickListener {
                val message = etChatGroup.text.toString()
                if (message.isEmpty()) etChatGroup.error = "Masukkan Pesan Anda!"
                else {
                    groupChatViewModel.insertNewChat(
                        groupId.toString(),
                        message,
                        this@ChatGroupActivity
                    )
                    etChatGroup.text.clear()
                }
            }

            groupChatViewModel.getDataChat().observe(this@ChatGroupActivity, {
                if (it.isNotEmpty()) {
                    adapter.setData(it)
                }
            })
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}