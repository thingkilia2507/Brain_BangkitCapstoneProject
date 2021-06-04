package com.thing.bangkit.soulmood.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.adapter.GroupNameViewAdapter
import com.thing.bangkit.soulmood.databinding.ActivityGroupNameBinding
import com.thing.bangkit.soulmood.model.ChatGroup
import com.thing.bangkit.soulmood.viewmodel.GroupChatViewModel
import java.util.*
import kotlin.collections.ArrayList

class GroupNameActivity : AppCompatActivity() {
    private lateinit var adapter: GroupNameViewAdapter
    private val groupChatViewModel: GroupChatViewModel by viewModels()
    private var binding: ActivityGroupNameBinding? = null
    private var dataGroupName  = ArrayList<ChatGroup>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupNameBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        supportActionBar?.title = "Ruang Cerita"
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
                            putExtra(getString(R.string.room_id), data.id)
                            putExtra(getString(R.string.room_name), data.group_name)
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
        groupChatViewModel.setGroupName()
        groupChatViewModel.getGroupName().observe(this@GroupNameActivity, {
            if (it != null) {
                adapter.setData(it)
                dataGroupName = it
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.search)?.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = "Cari Ruang Chat"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText?.length == 0){
                    adapter.searchGroupName(dataGroupName)
                }
                else {
                    var data = ArrayList<ChatGroup>()
                    for(i in 0 until dataGroupName.size){
                        if(dataGroupName[i].group_name.toLowerCase(Locale.ROOT).contains(newText!!.toLowerCase(Locale.ROOT))){
                            data.add(ChatGroup(dataGroupName[i].id,dataGroupName[i].group_name))
                        }
                    }
                        adapter.searchGroupName(data)
                }
                return true
            }

        })

        return true

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