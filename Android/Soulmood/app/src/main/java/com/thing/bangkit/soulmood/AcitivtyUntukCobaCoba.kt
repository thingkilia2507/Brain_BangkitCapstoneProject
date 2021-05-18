package com.thing.bangkit.soulmood

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.thing.bangkit.soulmood.activity.ChatGroupActivity
import com.thing.bangkit.soulmood.adapter.GroupNameViewAdapter
import com.thing.bangkit.soulmood.alarmreceiver.AlarmReceiver
import com.thing.bangkit.soulmood.apiservice.ApiConfig
import com.thing.bangkit.soulmood.databinding.ActivityAcitivtyUntukCobaCobaBinding
import com.thing.bangkit.soulmood.model.ChatGroup
import com.thing.bangkit.soulmood.viewmodel.GroupChatViewModel
import com.thing.bangkit.soulmood.viewmodel.MoodTrackerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AcitivtyUntukCobaCoba : AppCompatActivity() {
    private val groupChatViewModel:GroupChatViewModel by viewModels()
    private val moodTrackerViewModel: MoodTrackerViewModel by viewModels()
    private lateinit var binding:ActivityAcitivtyUntukCobaCobaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAcitivtyUntukCobaCobaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getQuoteOfTheDay()

        binding.apply {
            val adapter = GroupNameViewAdapter("homeFragment")
            rvGroupName.layoutManager = LinearLayoutManager(this@AcitivtyUntukCobaCoba,
                LinearLayoutManager.HORIZONTAL,false)
            rvGroupName.adapter = adapter

            adapter.setOnItemClickCallback(object :GroupNameViewAdapter.OnItemClickCallback{
                override fun onItemClick(data: ChatGroup) {
                    startActivity(Intent(this@AcitivtyUntukCobaCoba, ChatGroupActivity::class.java).apply {
                        putExtra(getString(R.string.room_id),data.id)
                        putExtra(getString(R.string.room_name),data.group_name)
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
                    val etGroupName = findViewById<EditText>(R.id.et_group_name)
                    val btnNewGroup = findViewById<Button>(R.id.btn_add_new_group)
                    btnNewGroup.setOnClickListener {
                        val groupName = etGroupName.text.toString()
                        if(groupName.isEmpty()) etGroupName.error = "Masukkan Nama Ruang!"
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
       // moodTrackerViewModel.insertMoodData(this)
    }



    private fun getQuoteOfTheDay() {
        val service = ApiConfig.getRetrofitQuotes()
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = service.getDialyQuote(1)
                withContext(Dispatchers.Main){
                    if(response.code() == 200){
                        if(response.body() != null){
                            AlarmReceiver().setRepeatingAlarmMotivationWord(
                                this@AcitivtyUntukCobaCoba,
                                "${response.body()?.quotes?.get(0)?.text.toString()} - ${response.body()?.quotes?.get(0)?.author.toString()} -"
                            )
                        }
                    }
                }
            }
            catch (e: Throwable){
                withContext(Dispatchers.Main) {
                    Log.v("retrofit error", e.message.toString())
                }
            }
        }
    }

}