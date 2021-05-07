package com.thing.bangkit.soulmood.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.thing.bangkit.soulmood.MainActivity
import com.thing.bangkit.soulmood.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashActivity, SplashActivity1::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}