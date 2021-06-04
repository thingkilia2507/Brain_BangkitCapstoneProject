package com.thing.bangkit.soulmood.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.helper.IProgressResult
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.helper.SharedPref
import java.util.*

class ProfileViewModel : ViewModel() {

    fun onChangeProfileInformation(
        context: Context,
        name: String,
        email: String,
        password: String,
        result: IProgressResult
    ) {
            val user = FirebaseAuth.getInstance().currentUser
            user?.let { it_user ->

                if(it_user.email.isNullOrEmpty()){
                    SharedPref.getPref(context, MyAsset.KEY_EMAIL)?.let {email_auth->
                        doUpdateInformation(email_auth, password, user, it_user, email, context, name, result)
                    }
                }

                it_user.email?.let {
                    doUpdateInformation(it, password, user, it_user, email, context, name, result)
                }


            }

    }

    private fun doUpdateInformation(
        it: String,
        password: String,
        user: FirebaseUser,
        it_user: FirebaseUser,
        email: String,
        context: Context,
        name: String,
        result: IProgressResult
    ): Task<Void> {
        val credential = EmailAuthProvider.getCredential(it, password)
        return user.reauthenticate(credential).addOnCompleteListener { task ->
            task.addOnSuccessListener {
                it_user.updateEmail(email).addOnCompleteListener { task ->
                    task.addOnSuccessListener {

                        SharedPref.setPref(
                            context,
                            MyAsset.KEY_EMAIL,
                            email.toLowerCase(Locale.ROOT)
                        )

                        SharedPref.getPref(context, MyAsset.KEY_USER_ID)?.let { id ->
                            FirebaseFirestore.getInstance().collection("users").document(id).update(
                                "email", it_user.email, "name", name
                            ).addOnSuccessListener {
                                SharedPref.setPref(context, MyAsset.KEY_NAME, name)
                                result.onSuccess(context.getString(R.string.changeProfileSuccess))

                            }.addOnFailureListener { exc ->
                                result.onFailure(exc.message.toString())
                            }
                        }
                    }.addOnFailureListener { exc ->
                        when {
                            exc.message.toString().equals(
                                "A network error (such as timeout, interrupted connection or unreachable host) has occurred.",
                                true
                            ) -> {
                                result.onFailure(context.getString(R.string.no_internet_connection))
                            }
                            exc.message.toString().equals(
                                "The password is invalid or the user does not have a password.",
                                true
                            ) -> {
                                result.onFailure(context.getString(R.string.wrong_password))
                            }
                            exc.message.toString().equals(
                                "The email address is already in use by another account.",
                                true
                            ) -> {
                                result.onFailure( context.getString(R.string.email_used))
                            }
                            else -> {
                                result.onFailure(exc.message.toString())
                            }
                        }
                        Log.d("TAGDATAKU", exc.message.toString())
                    }
                }
            }.addOnFailureListener { exc ->

                when {
                    exc.message.toString().equals(
                        "A network error (such as timeout, interrupted connection or unreachable host) has occurred.",
                        true
                    ) -> {
                        result.onFailure(context.getString(R.string.no_internet_connection))
                    }
                    exc.message.toString().equals(
                        "The password is invalid or the user does not have a password.",
                        true
                    ) -> {
                        result.onFailure(context.getString(R.string.wrong_password))
                    }
                    exc.message.toString().equals(
                        "The email address is already in use by another account.",
                        true
                    ) -> {
                        result.onFailure( context.getString(R.string.email_used))
                    }
                    else -> {
                        result.onFailure(exc.message.toString())
                    }
                }
                Log.d("TAGDATAKU", exc.message.toString())
            }
        }
    }
}