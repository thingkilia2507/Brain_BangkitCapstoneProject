package com.thing.bangkit.soulmood.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.thing.bangkit.soulmood.helper.DateHelper
import java.util.*
import kotlin.collections.HashMap

class RegisterViewModel : ViewModel() {
    private var auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    fun setData(name: String, email: String, password: String, gender: String,context: Context) {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
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
            Toast.makeText(context, "Berhasil register", Toast.LENGTH_SHORT).show()

        }.addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
    }
}