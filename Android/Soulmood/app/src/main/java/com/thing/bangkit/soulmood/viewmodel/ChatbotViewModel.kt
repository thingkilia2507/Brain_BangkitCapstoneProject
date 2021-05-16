package com.thing.bangkit.soulmood.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.thing.bangkit.soulmood.apiservice.ApiConfig
import com.thing.bangkit.soulmood.helper.DateHelper
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.helper.SharedPref
import com.thing.bangkit.soulmood.model.ChatbotMessage
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatbotViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private var messageReqReply : String = ""
    private var _chat = MutableLiveData<ArrayList<ChatbotMessage>>()
    private val _suggestionResponse = MutableLiveData<ArrayList<String>>()
    val chat : LiveData<ArrayList<ChatbotMessage>> = _chat
    val suggestionResponse : LiveData<ArrayList<String>> = _suggestionResponse
    private val currentDate : String= SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

    fun initChatbot(context: Context) {
        val name = SharedPref.getPref(context, MyAsset.KEY_NAME)
        viewModelScope.launch {
            reqChatbotMessagesData(context)
            sendMessage(context, "Halo, $name!")
            delay(2000)
            sendMessage(context, "Saya Soulmoo, teman curhatmu. Cerita yuk!")
        }
    }

     fun reqChatbotMessagesData(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = db.collection("db_chatbot").document("1.0").collection("user_chatbot")
                .document(SharedPref.getPref(context, MyAsset.KEY_USER_ID).toString()).collection("chatbot_messages").document(currentDate).collection("message")
                .orderBy("created_at", Query.Direction.ASCENDING)
            withContext(Dispatchers.Main){
                data.addSnapshotListener { value, _ ->
                    value?.let {
                        val chatData = ArrayList<ChatbotMessage>()
                        for (message in value.documents) {
                            chatData.add(
                                ChatbotMessage(
                                    id = message.getString("id").toString(),
                                    name = message.getString("name").toString(),
                                    email = message.getString("email").toString(),
                                    message = message.getString("message").toString(),
                                    created_at = message.getString("created_at").toString()
                                )
                            )
                        }
                        _chat.postValue(chatData)
                    }
                }
            }
        }
    }

    fun sendMessage(context : Context, message: String, name: String=MyAsset.SOULMOOD_CHATBOT_NAME, email: String="") {
        if(email != "") {
            messageReqReply += message + "\n"
        }

        val id = UUID.randomUUID().toString()
        viewModelScope.launch(Dispatchers.IO) {
            val database = db.collection("db_chatbot").document("1.0").collection("user_chatbot")
                .document(SharedPref.getPref(context, MyAsset.KEY_USER_ID)!!).collection("chatbot_messages").document(currentDate).collection("message")
                .document(id).set(
                    ChatbotMessage(
                        id,
                        name,
                        email,
                        message,
                        DateHelper.getCurrentDateTime()
                    )
                )
            withContext(Dispatchers.Main){
                database.addOnSuccessListener {
                    Log.d("TAGDATAKU", "sendMessage: message_sent" + message)
                }.addOnFailureListener {
                    Toasty.error(context, it.message.toString(), Toasty.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun reqChatbotReply(context: Context){
        val name = SharedPref.getPref(context, MyAsset.KEY_NAME)!!
        Log.d("TAGDATAKU", "reqChatbotReply isnotempty: ${messageReqReply.isNotEmpty()}")
        if(messageReqReply.isNotEmpty()){
            val apiService = ApiConfig.getRetrofitSoulmood()
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    val response = apiService.reqChatbotResponse(name, messageReqReply)
                    withContext(Dispatchers.Main){
                        if(response.code() == 200){
                            response.body()?.let {
                                for (reply in it.message) {
                                    Log.d("TAGDATAKU", "===MessageSend: message " + reply)
                                    sendMessage(context, reply)
                                }
                                _suggestionResponse.postValue(it.suggestion)
                                messageReqReply = ""
                            }
                        }else{
                            Log.d("TAGDATAKU", "reqChatbotReply: "+ response.code())
                            Toasty.error(context, "Maaf, Soulmood sedang dalam perbaikan.", Toasty.LENGTH_SHORT).show()
                        }
                    }
                }
                catch (e: Throwable){
                    withContext(Dispatchers.Main) {
                        Log.d("TAGDATAKU", "reqChatbotReply RetrofitFail: "+  e.message.toString())
                        Toasty.error(context, "Maaf, Soulmood sedang dalam perbaikan.", Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }

}