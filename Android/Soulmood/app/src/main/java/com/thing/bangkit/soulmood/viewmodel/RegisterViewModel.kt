package com.thing.bangkit.soulmood.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thing.bangkit.soulmood.helper.DateHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.HashMap

class RegisterViewModel : ViewModel() {
    private var auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private  var status =MutableLiveData<Boolean>()

    fun setData(
        name: String,
        email: String,
        password: String,
        gender: String,
        context: Context
    ): LiveData<Boolean> {
        viewModelScope.launch(Dispatchers.IO) {
            val firebaseAuth = auth.createUserWithEmailAndPassword(email, password)
            withContext(Dispatchers.Main) {
                firebaseAuth.addOnSuccessListener {
                    val id = UUID.randomUUID().toString()
                    val map = HashMap<String, String>()
                    map["id"] = id
                    map["name"] = name
                    map["email"] = auth.currentUser.email
                    map["gender"] = gender
                    map["created_at"] = DateHelper.getCurrentDate()
                    db.collection("users").document(id).set(
                        map
                    )
                    status.postValue(true)
                }.addOnFailureListener {
                    status.postValue(false)
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        return status
    }
}