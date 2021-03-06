package com.thing.bangkit.soulmood.activity

import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.adapter.GroupChatFirebaseViewAdapter
import com.thing.bangkit.soulmood.databinding.ActivityChatGroupBinding
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.viewmodel.GroupChatViewModel
import es.dmoral.toasty.Toasty

class ChatGroupActivity : AppCompatActivity() {
    private  var adapterFirebase:GroupChatFirebaseViewAdapter?=null
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
            Toasty.error(this,  getString(R.string.no_internet_connection), Toasty.LENGTH_SHORT).show()
        }
            groupChatViewModel.setDataChat(groupId.toString())
            binding?.apply {
                floatingSend.setOnClickListener {
                    val message = etChatGroup.text.toString()
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
                    if (it != null) {
                        val linearLayout = LinearLayoutManager(this@ChatGroupActivity)
                        //auto scroll recyclerview to bottom
                        linearLayout.reverseLayout = true
                        rvChatGroup.layoutManager = linearLayout
                        adapterFirebase = GroupChatFirebaseViewAdapter(it)
                        adapterFirebase?.startListening()
                        rvChatGroup.adapter = adapterFirebase
                        adapterFirebase?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                                super.onItemRangeInserted(positionStart, itemCount)
                                rvChatGroup.scrollToPosition(0)
                            }
                        })
                        progressBar.visibility = View.GONE
                    }
                })
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

    override fun onStart() {
        super.onStart()
        adapterFirebase?.startListening()

    }

    override fun onStop() {
        super.onStop()
        adapterFirebase?.stopListening()
    }
}