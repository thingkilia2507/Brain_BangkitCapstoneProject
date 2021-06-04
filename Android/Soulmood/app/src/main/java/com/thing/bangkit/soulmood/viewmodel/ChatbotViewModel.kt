package com.thing.bangkit.soulmood.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.thing.bangkit.soulmood.BuildConfig
import com.thing.bangkit.soulmood.helper.*
import com.thing.bangkit.soulmood.helper.DateHelper.currentDate
import com.thing.bangkit.soulmood.helper.MyAsset.CHATBOT_DB_NAME
import com.thing.bangkit.soulmood.model.ChatMessage
import com.thing.bangkit.soulmood.model.ChatbotMessage
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

class ChatbotViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private var messageReqReply : String = ""
    //private var _chat = MutableLiveData<ArrayList<ChatbotMessage>>()
    private var _chat = MutableLiveData<FirestoreRecyclerOptions<ChatbotMessage>>()
    private val _suggestionResponse = MutableLiveData<ArrayList<String>>()
    //val chat : LiveData<ArrayList<ChatbotMessage>> = _chat
    val chat : LiveData<FirestoreRecyclerOptions<ChatbotMessage>> = _chat
    val suggestionResponse : LiveData<ArrayList<String>> = _suggestionResponse


    fun initChatbot(context: Context) {
        val name = SharedPref.getPref(context, MyAsset.KEY_NAME)
        viewModelScope.launch {
            reqChatbotMessagesData(context)
            delay(1000)
         //   if(chat.value == null || chat.value!!.isEmpty()){
            if(chat.value == null){
                sendMessage(object : IFinishedListener{
                    override fun onFinish() {
                        //
                    }
                },context, "Halo, $name!")
                delay(1000)

                sendMessage(object : IFinishedListener {
                    override fun onFinish() {
                        //
                    }
                }, context, "Halo, aku adalah SoulMoo, bot yang bisa kamu ajak bicara tentang apa yg kamu rasakan dan pikirkan kapan saja.\n" +
                        "\n" +
                        "Apa yang ingin kamu bicarakan denganku hari ini?\uD83D\uDE0A")
            }else {
                sendMessage(object : IFinishedListener{
                    override fun onFinish() {
                        //
                    }
                },context, "Halo, $name!")
                delay(1000)

                //if(chat.value != null && chat.value!!.size <1){
                if(chat.value != null ){
                    sendMessage(object : IFinishedListener {
                        override fun onFinish() {
                            //
                        }
                    }, context, "Halo, aku adalah SoulMoo, bot yang bisa kamu ajak bicara tentang apa yg kamu rasakan dan pikirkan kapan saja.\n" +
                            "\n" +
                            "Apa yang ingin kamu bicarakan denganku hari ini?\uD83D\uDE0A")
                }else {
                    sendMessage(object : IFinishedListener {
                        override fun onFinish() {
                            //
                        }
                    }, context, "Saya Soulmoo, teman curhatmu. Cerita yuk!")
                }
            }

            _suggestionResponse.postValue(arrayListOf("Diri Sendiri", "Rumah", "Sekolah", "Pekerjaan", "Pertemanan", "Percintaan","Lainnya","Ngga dulu deh"))
        }
    }

     fun reqChatbotMessagesData(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = db.collection(CHATBOT_DB_NAME).document(BuildConfig.VERSION_NAME).collection("user_chatbot")
                .document(SharedPref.getPref(context, MyAsset.KEY_USER_ID).toString()).collection("chatbot_messages").document(currentDate).collection("message")
                .orderBy("created_at", Query.Direction.ASCENDING)
            withContext(Dispatchers.Main){
//                data.addSnapshotListener { value, _ ->
//                    value?.let {
//                        val chatData = ArrayList<ChatbotMessage>()
//                        for (message in value.documents) {
//                            chatData.add(
//                                ChatbotMessage(
//                                    id = message.getString("id").toString(),
//                                    name = message.getString("name").toString(),
//                                    email = message.getString("email").toString(),
//                                    message = message.getString("message").toString(),
//                                    created_at = message.getString("created_at").toString()
//                                )
//                            )
//                        }
//                        _chat.postValue(chatData)
//                    }
//                }
                val firebaseRecyclerOptions = FirestoreRecyclerOptions.Builder<ChatbotMessage>()
                    .setQuery(data , ChatbotMessage::class.java).build()
                _chat.postValue(firebaseRecyclerOptions)
            }
        }
    }

    fun sendMessage(finishedListener : IFinishedListener, context : Context, message: String, name: String=MyAsset.SOULMOOD_CHATBOT_NAME, email: String="") {
        if(email != "") {
            messageReqReply += message + "\n"
        }

        val id = UUID.randomUUID().toString()
        viewModelScope.launch(Dispatchers.IO) {
            val database = db.collection(CHATBOT_DB_NAME).document(BuildConfig.VERSION_NAME).collection("user_chatbot")
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
                    finishedListener.onFinish()
                    Log.d("TAGDATAKU", "sendMessage: message_sent " + message)
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
            val apiService = RetrofitBuild.instanceService()
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    val response = apiService.reqChatbotResponse(name, messageReqReply)
                    withContext(Dispatchers.Main){
                        if(response.code() == 200){
                            response.body()?.let {
                                recursionSendMessage(it.message, it.message.size-1, context)

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

    private fun recursionSendMessage(message: List<String>, indexMax: Int, context: Context) {
        if (message.size == indexMax+1) {
            sendMessage(object : IFinishedListener {
                override fun onFinish() {
                    if(indexMax == 0) return
                    recursionSendMessage(message.reversed(), indexMax-1, context)
                }
            }, context, message.reversed()[indexMax])
        }else{
            sendMessage(object : IFinishedListener {
                override fun onFinish() {
                    if(indexMax == 0) return
                    recursionSendMessage(message, indexMax-1, context)

                }
            }, context, message[indexMax])
        }


    }

}