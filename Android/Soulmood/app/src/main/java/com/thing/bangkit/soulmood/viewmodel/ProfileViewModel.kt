package com.thing.bangkit.soulmood.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.helper.IProgressResult
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.helper.SharedPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun onChangeProfileInformation(context: Context, name: String, email: String, password: String, result: IProgressResult) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = auth.currentUser
            user?.let{it_user->
                it_user.email?.let {
                    val credential = EmailAuthProvider.getCredential(it, password)
                    user.reauthenticate(credential).addOnCompleteListener { task ->
                        task.addOnSuccessListener {

                            it_user.updateEmail(email).addOnCompleteListener {
                                it.addOnSuccessListener {
                                    SharedPref.setPref(context, MyAsset.KEY_EMAIL, email)
                                    SharedPref.getPref(context, MyAsset.KEY_USER_ID)?.let { id ->
                                        db.collection("users").document(id).update(
                                            "email", it_user.email, "name", name
                                        ).addOnSuccessListener {
                                            SharedPref.setPref(context, MyAsset.KEY_NAME, name)
                                            result.onSuccess(context.getString(R.string.changeProfileSuccess))

                                        }.addOnFailureListener {
                                            result.onFailure(it.message.toString())
                                        }
                                    }
                                }.addOnFailureListener {
                                    result.onFailure(it.message.toString())
                                }
                            }
                        }.addOnFailureListener {
                            result.onFailure(context.getString(R.string.changepassword_failed_message))
                            Log.d("TAGDATAKU", it.message.toString())
                        }
                    }
                }
            }
        }
    }
}