package com.thing.bangkit.soulmood.helper

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SharedPref {
    private var PREFERENCE_KEY = "mySharedPreference"
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var value: String

    fun isNotNull(context: Context, key: String?): Boolean {
        initSharedPref(context)
        return sharedPreferences.getString(key, null) != null
    }

    fun setPref(context: Context, key: String, value: String) {
        initSharedPref(context)
        val editor = sharedPreferences.edit()
        editor.putString(key, value).apply()
    }

    fun getPref(context: Context, key: String): String? {
        initSharedPref(context)
        return sharedPreferences.getString(key, value)
    }

    fun clearPref(context: Context) {
        initSharedPref(context)

        val editor = sharedPreferences.edit()
        editor.clear().apply()
    }

    fun clearOnePref(context: Context, key: String?) {
        initSharedPref(context)
        val editor = sharedPreferences.edit()
        editor.remove(key).apply()
    }

    private fun initSharedPref(context: Context) {
        val masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            PREFERENCE_KEY,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}