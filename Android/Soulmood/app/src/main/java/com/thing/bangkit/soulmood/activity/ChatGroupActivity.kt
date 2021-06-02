package com.thing.bangkit.soulmood.activity

import android.content.Intent
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.adapter.GroupChatViewAdapter
import com.thing.bangkit.soulmood.databinding.ActivityChatGroupBinding
import com.thing.bangkit.soulmood.databinding.NoInternetLayoutBinding
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.viewmodel.GroupChatViewModel

class ChatGroupActivity : AppCompatActivity() {
    private var noInternetBinding : NoInternetLayoutBinding? = null
    private var checkInternet: NetworkCapabilities? = null
    private val groupChatViewModel: GroupChatViewModel by viewModels()
    private var binding: ActivityChatGroupBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatGroupBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val groupId = intent.getStringExtra(getString(R.string.room_id))
        val groupName = intent.getStringExtra(getString(R.string.room_name))
        supportActionBar?.title = groupName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //check internet connection
        checkInternet = MyAsset.checkInternetConnection(this)
        if(checkInternet == null){
            noInternetBinding = NoInternetLayoutBinding.inflate(layoutInflater)
            setContentView(noInternetBinding?.root)
            noInternetBinding?.btnTryAgain?.setOnClickListener {
                checkInternet = MyAsset.checkInternetConnection(this)
                if (checkInternet != null) {
                    val intent = Intent(this, ChatbotActivity::class.java)
                    intent.putExtra(getString(R.string.room_id),groupId)
                    intent.putExtra(getString(R.string.room_name),groupName)
                    startActivity(intent)
                    finish()
                }
            }
        }else{
            groupChatViewModel.setDataChat(groupId.toString())

            binding?.apply {
                val adapter = GroupChatViewAdapter(this@ChatGroupActivity)
                val linearLayout = LinearLayoutManager(this@ChatGroupActivity)
                //auto scroll recyclerview to bottom
                linearLayout.reverseLayout = true
                rvChatGroup.layoutManager = linearLayout
                rvChatGroup.adapter = adapter

                floatingSend.setOnClickListener {
                    val message = etChatGroup.text.toString()
                    //if (message.isEmpty()) etChatGroup.error = "Masukkan Pesan Anda!"
                    if(message.isNotEmpty()) {
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



    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        checkInternet = null
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}