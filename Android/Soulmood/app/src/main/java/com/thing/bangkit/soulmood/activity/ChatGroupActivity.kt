package com.thing.bangkit.soulmood.activity

import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.adapter.GroupChatFirebaseViewAdapter
import com.thing.bangkit.soulmood.adapter.GroupChatViewAdapter
import com.thing.bangkit.soulmood.databinding.ActivityChatGroupBinding
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.model.ChatMessage
import com.thing.bangkit.soulmood.viewmodel.GroupChatViewModel
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
               // val adapter = GroupChatViewAdapter()


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
                    if (it != null) {
                        val linearLayout = LinearLayoutManager(this@ChatGroupActivity)
                        //auto scroll recyclerview to bottom
                        linearLayout.reverseLayout = true
                        binding?.rvChatGroup?.layoutManager = linearLayout
                        adapterFirebase = GroupChatFirebaseViewAdapter(it)
                        adapterFirebase?.startListening()
                        binding?.rvChatGroup?.adapter = adapterFirebase

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