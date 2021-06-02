package com.thing.bangkit.soulmood.activity

import android.content.Intent
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.adapter.ChatbotAdapter
import com.thing.bangkit.soulmood.adapter.ChatbotFirebaseAdapter
import com.thing.bangkit.soulmood.adapter.SuggestionChatAdapter
import com.thing.bangkit.soulmood.databinding.ActivityChatbotBinding
import com.thing.bangkit.soulmood.databinding.NoInternetLayoutBinding
import com.thing.bangkit.soulmood.helper.IClickedItemListener
import com.thing.bangkit.soulmood.helper.IFinishedListener
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.helper.SharedPref
import com.thing.bangkit.soulmood.viewmodel.ChatbotViewModel

class ChatbotActivity : AppCompatActivity(), IClickedItemListener {

    private val viewModel: ChatbotViewModel by viewModels()
    private var binding : ActivityChatbotBinding? = null
    private var noInternetBinding : NoInternetLayoutBinding? = null
    private var checkInternet: NetworkCapabilities? = null

    //private val adapter = ChatbotAdapter()
    private var adapter : ChatbotFirebaseAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //check internet connection
        checkInternet = MyAsset.checkInternetConnection(this)
        if(checkInternet == null){
            noInternetBinding = NoInternetLayoutBinding.inflate(layoutInflater)
            setContentView(noInternetBinding?.root)
            noInternetBinding?.btnTryAgain?.setOnClickListener {
                checkInternet = MyAsset.checkInternetConnection(this)
                if (checkInternet != null) {
                    val intent = Intent(this, ChatbotActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }else{
            binding?.apply {
                rvChatbot.layoutManager = LinearLayoutManager(this@ChatbotActivity)

                viewModel.initChatbot(this@ChatbotActivity)

                viewModel.chat.observe(this@ChatbotActivity, {
                   // adapter.setData(it)
                   adapter = ChatbotFirebaseAdapter(it)
                    adapter?.startListening()
                    rvChatbot.adapter = adapter
                   // rvChatbot.scrollToPosition(it.size-1)
                })

                ivBack.setOnClickListener {
                    onBackPressed()
                }
                fabChatbotSend.setOnClickListener {
                    if(etChatbot.text.toString().isNotEmpty()) {
                        viewModel.sendMessage(object : IFinishedListener{
                            override fun onFinish() {
                                viewModel.reqChatbotReply(this@ChatbotActivity)
                                binding?.rvSuggestionMessage?.visibility = GONE
                            }

                        },this@ChatbotActivity,etChatbot.text.toString(), SharedPref.getPref(this@ChatbotActivity, MyAsset.KEY_NAME)!!, SharedPref.getPref(this@ChatbotActivity, MyAsset.KEY_EMAIL)!!)
                        etChatbot.text.clear()

                    }

                }

                viewModel.suggestionResponse.observe(this@ChatbotActivity, {
                    if (it.size > 0) {
                        binding?.apply{
                            val suggestionAdapter = SuggestionChatAdapter(this@ChatbotActivity)
                            suggestionAdapter.suggestionMessage = it
                            rvSuggestionMessage.adapter = suggestionAdapter
                            rvSuggestionMessage.visibility = View.VISIBLE
                        }
                    }else{
                        binding?.apply{
                            rvSuggestionMessage.visibility = GONE
                        }
                    }
                })
            }
        }



    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        noInternetBinding = null
    }

    override fun onClicked(message: String) {
        viewModel.sendMessage( object :IFinishedListener{
            override fun onFinish() {
                viewModel.reqChatbotReply(this@ChatbotActivity)
                binding?.rvSuggestionMessage?.visibility = GONE
            }
        },this@ChatbotActivity,message, SharedPref.getPref(this@ChatbotActivity, MyAsset.KEY_NAME)!!, SharedPref.getPref(this@ChatbotActivity, MyAsset.KEY_EMAIL)!!)
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }
}