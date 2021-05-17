package com.thing.bangkit.soulmood.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.apiservice.ApiConfig
import com.thing.bangkit.soulmood.helper.DateHelper
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.helper.SharedPref
import com.thing.bangkit.soulmood.model.ChatGroup
import com.thing.bangkit.soulmood.model.ChatMessage
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GroupChatViewModel : ViewModel() {
    private var auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var groupNameData = MutableLiveData<ArrayList<ChatGroup>>()
    private var groupChatData = MutableLiveData<ArrayList<ChatMessage>>()

    fun insertNewGroup(groupName: String, context: Context) {
        val id = UUID.randomUUID().toString()
        val map = HashMap<String, String>()
        map["id"] = id
        map["group_name"] = groupName
        map["created_at"] = DateHelper.getCurrentDateTime()
        map["created_by"] = auth.currentUser?.email.toString()
        viewModelScope.launch(Dispatchers.IO) {
            val database = db.collection("groups_chat").document(id).set(map)
            withContext(Dispatchers.Main) {
                database.addOnSuccessListener {
                    Toasty.success(context, context.getString(R.string.room_added), Toasty.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toasty.error(context, it.message.toString(), Toasty.LENGTH_SHORT).show()
                }
            }
        }
    }

    //get group name data and add to livedata
    fun setGroupName(type:String?=null) {
        viewModelScope.launch(Dispatchers.IO) {
            val database = db.collection("groups_chat").orderBy("created_at", Query.Direction.DESCENDING)
            withContext(Dispatchers.Main) {
                if(type == MyAsset.GROUP_NAME_ACTIVITY) database.orderBy("group_name",Query.Direction.ASCENDING)
                database.addSnapshotListener { value, _ ->
                    if (value != null) {
                        var group = ArrayList<ChatGroup>()
                        for (data in value.documents) {
                            group.add(
                                ChatGroup(
                                    data.getString("id").toString(),
                                    data.getString("group_name").toString()
                                )
                            )
                        }
                        groupNameData.postValue(group)
                    }
                }
            }
        }

    }

    fun getGroupName(): LiveData<ArrayList<ChatGroup>> {
        return groupNameData
    }

    fun insertNewChat(group_id: String, message: String, context: Context) {
        val service = ApiConfig.getRetrofitSoulmood()
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = service.checkBadWordResponse(message)
                withContext(Dispatchers.Main){
                    if(response.code() == 200){
                        if(response.body() != null){
                            var aiMessage=""
                            var status="true"
                            if(response.body()!!.status){
                                aiMessage = response.body()!!.message
                                status = "true"
                            }else{
                                aiMessage = "*Pesan ini mengandung kata kasar*"
                                status = "false"
                            }
                            val id = UUID.randomUUID().toString()
                            viewModelScope.launch(Dispatchers.IO) {
                                val database = db.collection("groups_chat")
                                    .document(group_id).collection("message").add(
                                        ChatMessage(id, SharedPref.getPref(context, MyAsset.KEY_NAME).toString(),
                                            SharedPref.getPref(context, MyAsset.KEY_EMAIL), message, aiMessage, DateHelper.getCurrentDateTime(), ""
                                        ,status)
                                    )
                                withContext(Dispatchers.Main){
                                    database.addOnSuccessListener {
                                    }.addOnFailureListener {
                                        Toasty.error(context, it.message.toString(), Toasty.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }else{
                        Toasty.error(context, "Maaf, Soulmood sedang dalam perbaikan.", Toasty.LENGTH_SHORT).show()
                    }
                }
            }catch (e:Throwable){
                Log.d("TAGDATAKU", "reqChatbotReply RetrofitFail: "+  e.message.toString())
                Toasty.error(context, "Maaf, Soulmood sedang dalam perbaikan.", Toasty.LENGTH_SHORT).show()
            }

        }




    }

    //get chat data by groupId, then set data to livedata
    fun setDataChat(group_id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val database =db.collection("groups_chat").document(group_id).collection("message")
                .orderBy("created_at", Query.Direction.DESCENDING)
            withContext(Dispatchers.Main){
                database.addSnapshotListener { value, _ ->
                    if (value != null) {
                        var chatData = ArrayList<ChatMessage>()
                        for (data in value.documents) {
                            chatData.add(
                                ChatMessage(
                                    id = data.getString("id").toString(),
                                    sender = data.getString("sender").toString(),
                                    ori_message = data.getString("ori_message").toString(),
                                    ai_message = data.getString("ai_message").toString(),
                                    email = data.getString("email").toString(),
                                    created_at = data.getString("created_at").toString(),
                                    status = data.getString("status").toString()
                                )
                            )
                        }
                        groupChatData.postValue(chatData)
                    }
                }
            }
        }


    }

    fun getDataChat(): LiveData<ArrayList<ChatMessage>> {
        return groupChatData
    }

    fun getQuoteOfTheDay():LiveData<String>{
        var quoteMessage = MutableLiveData<String>()
        val service = ApiConfig.getRetrofitQuotes()
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = service.getDialyQuote(1)
                withContext(Dispatchers.Main){
                    if(response.code() == 200){
                        if(response.body() != null){
                            response.body()?.quotes?.get(0)?.let {
                                quoteMessage.postValue("${it.text} \n- ${it.author} -")
                            }
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
        return quoteMessage
    }

}