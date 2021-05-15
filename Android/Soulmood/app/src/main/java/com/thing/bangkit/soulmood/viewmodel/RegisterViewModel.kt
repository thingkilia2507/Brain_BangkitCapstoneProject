package com.thing.bangkit.soulmood.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.helper.DateHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.HashMap

class RegisterViewModel : ViewModel() {
    private var auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var status = MutableLiveData<Boolean>()

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
                    map["email"] = auth.currentUser?.email.toString()
                    map["gender"] = gender
                    map["created_at"] = DateHelper.getCurrentDateTime()
                    db.collection("users").document(id).set(
                        map
                    )
                    status.postValue(true)
                }.addOnFailureListener {
                    status.postValue(false)
                    it.message?.let { message->
                        if (message.equals("The given password is invalid. [ Password should be at least 6 characters ]",true)) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.invalid_password_message),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        return status
    }
}