package com.thing.bangkit.soulmood.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.adapter.GroupNameViewAdapter
import com.thing.bangkit.soulmood.databinding.ActivityGroupNameBinding
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.model.ChatGroup
import com.thing.bangkit.soulmood.viewmodel.GroupChatViewModel

class GroupNameActivity : AppCompatActivity() {
    private lateinit var adapter: GroupNameViewAdapter
    private val groupChatViewModel: GroupChatViewModel by viewModels()
    private var binding: ActivityGroupNameBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupNameBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        supportActionBar?.title = "Daftar Grup"
        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        initAdapter()
        observeGroupChatViewModel()

        binding?.apply {
            adapter.setOnItemClickCallback(object : GroupNameViewAdapter.OnItemClickCallback {
                override fun onItemClick(data: ChatGroup) {
                    startActivity(
                        Intent(
                            this@GroupNameActivity,
                            ChatGroupActivity::class.java
                        ).apply {
                            putExtra(getString(R.string.group_id), data.id)
                            putExtra(getString(R.string.group_name), data.group_name)
                        })
                }
            })
        }
    }

    private fun initAdapter() {
        binding?.apply {
            adapter = GroupNameViewAdapter("groupNameActivity")
            rvGroupName.layoutManager = GridLayoutManager(
                this@GroupNameActivity,2
            )
            rvGroupName.adapter = adapter
        }
    }

    private fun observeGroupChatViewModel() {
        groupChatViewModel.setGroupName(MyAsset.GROUP_NAME_ACTIVITY)
        groupChatViewModel.getGroupName().observe(this@GroupNameActivity, {
            if (it != null) {
                adapter.setData(it)
            }
        })
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