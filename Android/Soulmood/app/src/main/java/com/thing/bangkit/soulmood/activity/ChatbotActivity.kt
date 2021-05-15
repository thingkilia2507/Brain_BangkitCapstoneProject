package com.thing.bangkit.soulmood.activity

import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.thing.bangkit.soulmood.adapter.ChatbotAdapter
import com.thing.bangkit.soulmood.adapter.SuggestionChatAdapter
import com.thing.bangkit.soulmood.databinding.ActivityChatbotBinding
import com.thing.bangkit.soulmood.helper.KeyboardEventListener
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.helper.SharedPref
import com.thing.bangkit.soulmood.viewmodel.ChatbotViewModel

class ChatbotActivity : AppCompatActivity() {

    private val viewModel: ChatbotViewModel by viewModels()
    private var binding : ActivityChatbotBinding? = null
    private val adapter = ChatbotAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.apply {
            rvChatbot.layoutManager = LinearLayoutManager(this@ChatbotActivity)
            rvChatbot.adapter = adapter

            viewModel.initChatbot(this@ChatbotActivity)

            viewModel.chat.observe(this@ChatbotActivity, {
                adapter.setData(it)
            })

            ivBack.setOnClickListener {
                onBackPressed()
            }
            fabChatbotSend.setOnClickListener {
                if(etChatbot.text.toString().isNotEmpty()) {
                    viewModel.sendMessage(this@ChatbotActivity,etChatbot.text.toString(), SharedPref.getPref(this@ChatbotActivity, MyAsset.KEY_NAME)!!, SharedPref.getPref(this@ChatbotActivity, MyAsset.KEY_EMAIL)!!)
                    etChatbot.text.clear()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        Log.d("TAGDATAKU", "onResume: call")
        KeyboardEventListener(this) { isOpen ->
            if (!isOpen) {
                val suggestionResponse = viewModel.reqChatbotReply(this)
                if (suggestionResponse.size > 0) {
                    binding?.apply{
                        val suggestionAdapter = SuggestionChatAdapter(::onSuggestionClicked)
                        suggestionAdapter.suggestionMessage = suggestionResponse
                        rvSuggestionMessage.adapter = suggestionAdapter
                        rvSuggestionMessage.visibility = VISIBLE

                    }
                }else{
                    binding?.apply{
                        rvSuggestionMessage.visibility = GONE
                    }
                }
                Log.d("TAGDATAKU", "onResume4: "+isOpen)
            }else{
                Log.d("TAGDATAKU", "onResume3: "+isOpen)
            }
        }
        Log.d("TAGDATAKU", "onResume: call2")
    }

    private fun onSuggestionClicked() {
        binding?.rvSuggestionMessage?.visibility = GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}