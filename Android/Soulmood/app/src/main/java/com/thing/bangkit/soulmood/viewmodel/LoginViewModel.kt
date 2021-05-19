package com.thing.bangkit.soulmood.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.helper.IProgressResult
import com.thing.bangkit.soulmood.model.UserData
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {
    private var auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var dataUser = MutableLiveData<UserData>()
    private  var status =MutableLiveData<Boolean>()


    fun login(email: String, password: String,context: Context, progressResult: IProgressResult):LiveData<UserData> {
        viewModelScope.launch(Dispatchers.IO) {
            val firebaseAuth = auth.signInWithEmailAndPassword(email, password)
            withContext(Dispatchers.Main){
                firebaseAuth.addOnSuccessListener {
                    db.collection("users").whereEqualTo("email",email.toLowerCase())
                        .addSnapshotListener { value, _ ->
                            val data = value?.documents
                            if(data?.isNotEmpty() == true){
                                for (data1 in data) {
                                    dataUser.postValue(UserData( data1.getString("id").toString(),
                                        data1.getString("email").toString(),
                                        data1.getString("name").toString()
                                    ))
                                }
                                progressResult.onSuccess("")
                            }else{
                                progressResult.onFailure("")
                                Toasty.error(context,  context.getString(R.string.wrong_email_input_format), Toasty.LENGTH_SHORT).show()
                            }
                        }

                }.addOnFailureListener {
                    progressResult.onFailure("")
                    Log.d("TAGDATAKU", "login: "+it.message.toString())
                    if(it.message.toString().equals("The password is invalid or the user does not have a password.",true)){
                        Toasty.error(context, context.getString(R.string.wrong_password), Toasty.LENGTH_SHORT).show()
                    }else if(it.message.toString().equals("There is no user record corresponding to this identifier. The user may have been deleted.",true)){
                        Toasty.error(context,  context.getString(R.string.wrong_email_login), Toasty.LENGTH_SHORT).show()
                    }else if(it.message.toString().equals("The email address is badly formatted.",true)){
                        Toasty.error(context,  context.getString(R.string.wrong_email_input_format), Toasty.LENGTH_SHORT).show()
                    }else {
                        Toasty.error(context, it.message.toString(), Toasty.LENGTH_SHORT).show()
                    }
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
                    if(it.message.toString().equals("There is no user record corresponding to this identifier. The user may have been deleted.",true)){
                        Toasty.error(context,  context.getString(R.string.wrong_email_login), Toasty.LENGTH_SHORT).show()
                    }else if(it.message.toString().equals("The email address is badly formatted.",true)){
                        Toasty.error(context,  context.getString(R.string.wrong_email_input_format), Toasty.LENGTH_SHORT).show()
                    }else {
                        Toasty.error(context, it.message.toString(), Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return status
    }
}