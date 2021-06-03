package com.thing.bangkit.soulmood.activity

import android.content.Intent
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.adapter.ChatbotAdapter
import com.thing.bangkit.soulmood.adapter.ChatbotFirebaseAdapter
import com.thing.bangkit.soulmood.adapter.SuggestionChatAdapter
import com.thing.bangkit.soulmood.databinding.ActivityChatbotBinding
import com.thing.bangkit.soulmood.helper.IClickedItemListener
import com.thing.bangkit.soulmood.helper.IFinishedListener
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.helper.SharedPref
import com.thing.bangkit.soulmood.viewmodel.ChatbotViewModel
import es.dmoral.toasty.Toasty

class ChatbotActivity : AppCompatActivity(), IClickedItemListener {

    private val viewModel: ChatbotViewModel by viewModels()
    private var binding: ActivityChatbotBinding? = null
    private var checkInternet: NetworkCapabilities? = null


    //private val adapter = ChatbotAdapter()
    private var adapter : ChatbotFirebaseAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //check internet connection
        checkInternet = MyAsset.checkInternetConnection(this)
        if (checkInternet == null) {
            Toasty.error(this, getString(R.string.no_internet_connection), Toasty.LENGTH_SHORT)
                .show()
        }
        binding?.apply {
            rvChatbot.layoutManager = LinearLayoutManager(this@ChatbotActivity)
            rvChatbot.adapter = adapter

            viewModel.initChatbot(this@ChatbotActivity)

            viewModel.chat.observe(this@ChatbotActivity, {
                // adapter.setData(it)
                adapter = ChatbotFirebaseAdapter(it)
                adapter?.startListening()
                rvChatbot.adapter = adapter
                //   rvChatbot.scrollToPosition(it.size-1)
                adapter?.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        super.onItemRangeInserted(positionStart, itemCount)
                        rvChatbot.scrollToPosition(positionStart)
                    }
                })
            })

            ivBack.setOnClickListener {
                onBackPressed()
            }
            fabChatbotSend.setOnClickListener {
                if (etChatbot.text.toString().isNotEmpty()) {
                    viewModel.sendMessage(
                        object : IFinishedListener {
                            override fun onFinish() {
                                viewModel.reqChatbotReply(this@ChatbotActivity)
                                binding?.rvSuggestionMessage?.visibility = GONE
                            }

                        },
                        this@ChatbotActivity,
                        etChatbot.text.toString(),
                        SharedPref.getPref(this@ChatbotActivity, MyAsset.KEY_NAME)!!,
                        SharedPref.getPref(this@ChatbotActivity, MyAsset.KEY_EMAIL)!!
                    )
                    etChatbot.text.clear()

                }

            }

            viewModel.suggestionResponse.observe(this@ChatbotActivity, {
                if (it.size > 0) {
                    binding?.apply {
                        val suggestionAdapter = SuggestionChatAdapter(this@ChatbotActivity)
                        suggestionAdapter.suggestionMessage = it
                        rvSuggestionMessage.adapter = suggestionAdapter
                        rvSuggestionMessage.visibility = View.VISIBLE
                    }
                } else {
                    binding?.apply {
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
        viewModel.sendMessage(
            object : IFinishedListener {
                override fun onFinish() {
                    viewModel.reqChatbotReply(this@ChatbotActivity)
                    binding?.rvSuggestionMessage?.visibility = GONE
                }
            },
            this@ChatbotActivity,
            message,
            SharedPref.getPref(this@ChatbotActivity, MyAsset.KEY_NAME)!!,
            SharedPref.getPref(this@ChatbotActivity, MyAsset.KEY_EMAIL)!!
        )
    }
}