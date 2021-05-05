package com.thing.bangkit.soulmood.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thing.bangkit.soulmood.helper.DateHelper
import com.thing.bangkit.soulmood.model.ChatGroup
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GroupNameViewModel:ViewModel() {
    private var auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var groupData=MutableLiveData<ArrayList<ChatGroup>>()
    fun insertNewGroup(groupName:String,context: Context){
        val id = UUID.randomUUID().toString()
        val map = HashMap<String, String>()
        map["id"] = id
        map["group_name"] = groupName
        map["created_at"] = DateHelper.getCurrentDate()
        map["created_by"] =auth.currentUser.email
        db.collection("groups_chat").document(id).set(map).addOnSuccessListener {
            Toast.makeText(context, "Grup Berhasil ditambah", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
    }
    
    fun setGroupName(){
        db.collection("groups_chat").addSnapshotListener { value, error ->
         if(value != null){
             var group = ArrayList<ChatGroup>()
             for (data in value.documents){
                group.add(ChatGroup(data.getString("id").toString(),data.getString("group_name").toString()))
             }
             groupData.postValue(group)
         }
        }
    }
    
    fun getGroupName():LiveData<ArrayList<ChatGroup>>{
        return groupData
    }
}