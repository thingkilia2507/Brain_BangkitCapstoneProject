package com.thing.bangkit.soulmood.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.thing.bangkit.soulmood.MainActivity
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.utils.SharedPref

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            if(SharedPref.isNotNull(this@SplashActivity,getString(R.string.email))){
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this@SplashActivity, SplashActivity1::class.java)
                startActivity(intent)
                finish()
            }
        }, 2000)
    }
}