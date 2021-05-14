package com.thing.bangkit.soulmood.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.helper.IProgressResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChangePasswordViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    fun onChangePassword(context: Context, oldPassword: String, newPassword: String, resultI: IProgressResult) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = auth.currentUser

            user?.email?.apply {
                val credential = EmailAuthProvider.getCredential(this, oldPassword)
                user.reauthenticate(credential).addOnCompleteListener { task ->
                    task.addOnSuccessListener {
                        user.updatePassword(newPassword).addOnCompleteListener {
                            it.addOnSuccessListener {
                                resultI.onSuccess(context.getString(R.string.change_password_succes))

                            }.addOnFailureListener { exception ->
                                Log.d("TAGDATAKU", "onChangePassword: "+ exception.message.toString())
                                Log.d("TAGDATAKU", "onChangePassword: "+ exception.message.toString())
                                resultI.onFailure(context.getString(R.string.invalid_password_message))
                            }
                        }
                    }.addOnFailureListener {
                        resultI.onFailure(context.getString(R.string.changepassword_failed_message))
                        Log.d("TAGDATAKU", it.message.toString())
                    }
                }
            }
        }
    }
}