package com.thing.bangkit.soulmood.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.helper.*
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
  private var groupChatData = MutableLiveData<FirestoreRecyclerOptions<ChatMessage>>()

    fun insertNewGroup(groupName: String, context: Context) {
        val id = UUID.randomUUID().toString()
        val map = HashMap<String, String>()
        map["id"] = id
        map["group_name"] = groupName
        map["created_at"] = DateHelper.getCurrentDateTime()
        map["created_by"] = auth.currentUser?.email.toString().toLowerCase(Locale.ROOT)
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
            var database:Query
            withContext(Dispatchers.Main) {
                if(type == MyAsset.HOME_FRAGMENT) {
                    database = db.collection("groups_chat").orderBy("created_at",Query.Direction.DESCENDING).limit(20)
                }
                else{
                    database=db.collection("groups_chat").orderBy("created_at",Query.Direction.DESCENDING)
                }
                database.addSnapshotListener { value, _ ->
                    if (value != null) {
                        val group = ArrayList<ChatGroup>()
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
        val service = RetrofitBuild.instanceBadwordService()
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = service.checkBadWordResponse(message)
                withContext(Dispatchers.Main){
                    if(response.code() == 200){
                        if(response.body() != null){
                            val aiMessage : String
                            val status : String
                            if(!response.body()!!.status){
                                aiMessage = message
                                status = "false"
                            }else{
                                aiMessage = "*${response.body()!!.message}*"
                                status = "true"
                            }
                            Log.d("TAGDATAKU : ",status)
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
                withContext(Dispatchers.Main){
                    Log.d("TAGDATAKU", "reqChatbotReply RetrofitFail: "+  e.message.toString())
                    Toasty.error(context, "Maaf, Soulmood sedang dalam perbaikan.", Toasty.LENGTH_SHORT).show()
                }
            }

        }

    }

    //get chat data by groupId, then set data to livedata
    fun setDataChat(group_id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val database =db.collection("groups_chat").document(group_id).collection("message")
                .orderBy("created_at", Query.Direction.DESCENDING)
            withContext(Dispatchers.Main){
                val firebaseRecyclerOptions = FirestoreRecyclerOptions.Builder<ChatMessage>()
                    .setQuery(database , ChatMessage::class.java).build()
                groupChatData.postValue(firebaseRecyclerOptions)

                // database.addSnapshotListener { value, _ ->
//                   if (value != null) {
//                        var chatData = ArrayList<ChatMessage>()
//                        for (data in value.documents) {
//                            chatData.add(
//                                ChatMessage(
//                                    id = data.getString("id").toString(),
//                                    sender = data.getString("sender").toString(),
//                                    ori_message = data.getString("ori_message").toString(),
//                                    ai_message = data.getString("ai_message").toString(),
//                                    email = data.getString("email").toString(),
//                                    created_at = data.getString("created_at").toString(),
//                                    status = data.getString("status").toString()
//                                )
//                            )
//                        }
//                    }
                }
            }
        }




    fun getDataChat(): LiveData<FirestoreRecyclerOptions<ChatMessage>> {
        return groupChatData
    }

    fun getQuoteOfTheDay(context:Context,status:IProgressResult?=null):LiveData<String>{
        val quoteMessage = MutableLiveData<String>()
        val service = RetrofitBuild.instanceQuotesService()
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = service.getDialyQuote()
                withContext(Dispatchers.Main) {
                    if (response.code() == 200) {
                        response.body()?.let {

                            if (it.author.isNotEmpty()) {
                                quoteMessage.postValue("${it.content} \n- ${it.author} -")
                            } else {
                                quoteMessage.postValue(it.content)
                            }
                            status?.onSuccess("")
                        }
                    }else{
                        status?.onFailure(context.getString(R.string.motivation_word_notavailable))
                    }
                }
            }
            catch (e: Throwable){
                withContext(Dispatchers.Main) {
                    status?.onFailure(context.getString(R.string.motivation_word_notavailable))
                    Log.v("retrofit error", e.message.toString())
                }
            }
        }
        return quoteMessage
    }

}