package com.thing.bangkit.soulmood.activity

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.thing.bangkit.soulmood.adapter.ChatbotAdapter
import com.thing.bangkit.soulmood.adapter.SuggestionChatAdapter
import com.thing.bangkit.soulmood.databinding.ActivityChatbotBinding
import com.thing.bangkit.soulmood.helper.IClickedItemListener
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.helper.SharedPref
import com.thing.bangkit.soulmood.viewmodel.ChatbotViewModel

class ChatbotActivity : AppCompatActivity(), IClickedItemListener {

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
                rvChatbot.scrollToPosition(it.size-1)
            })

            ivBack.setOnClickListener {
                onBackPressed()
            }
            fabChatbotSend.setOnClickListener {
                if(etChatbot.text.toString().isNotEmpty()) {
                    viewModel.sendMessage(this@ChatbotActivity,etChatbot.text.toString(), SharedPref.getPref(this@ChatbotActivity, MyAsset.KEY_NAME)!!, SharedPref.getPref(this@ChatbotActivity, MyAsset.KEY_EMAIL)!!)
                    etChatbot.text.clear()
                    viewModel.reqChatbotReply(this@ChatbotActivity)
                    binding?.rvSuggestionMessage?.visibility = GONE
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

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onClicked(message: String) {
        viewModel.sendMessage(this@ChatbotActivity,message, SharedPref.getPref(this@ChatbotActivity, MyAsset.KEY_NAME)!!, SharedPref.getPref(this@ChatbotActivity, MyAsset.KEY_EMAIL)!!)
        viewModel.reqChatbotReply(this@ChatbotActivity)
        binding?.rvSuggestionMessage?.visibility = GONE
    }
}