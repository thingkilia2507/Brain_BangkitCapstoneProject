package com.thing.bangkit.soulmood.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.thing.bangkit.soulmood.helper.DateHelper
import com.thing.bangkit.soulmood.model.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.HashMap

class LoginViewModel : ViewModel() {
    private var auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val dataUser = MutableLiveData<UserData>()
    private  var status =MutableLiveData<Boolean>()


    fun login(email: String, password: String,context: Context):LiveData<UserData> {
        viewModelScope.launch(Dispatchers.IO) {
            val firebaseAuth = auth.signInWithEmailAndPassword(email, password)
            withContext(Dispatchers.Main){
                firebaseAuth.addOnSuccessListener {
                    db.collection("users").whereEqualTo("email",email)
                        .addSnapshotListener { value, error ->
                            val data = value?.documents
                            if(data != null){
                                for (data1 in data) {
                                    dataUser.postValue(UserData( data1.getString("id").toString(),
                                        data1.getString("email").toString(),
                                        data1.getString("name").toString()
                                    ))
                                }
                            }
                        }
                }.addOnFailureListener {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        return dataUser
    }

    fun forgotPassword(email: String, context: Context):LiveData<Boolean>{
        viewModelScope.launch(Dispatchers.IO) {
            val firebaseAuth = auth.sendPasswordResetEmail(email)
            withContext(Dispatchers.Main){
                firebaseAuth.addOnSuccessListener {
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