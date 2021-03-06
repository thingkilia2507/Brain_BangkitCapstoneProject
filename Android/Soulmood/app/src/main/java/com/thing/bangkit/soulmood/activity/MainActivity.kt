package com.thing.bangkit.soulmood.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.alarmreceiver.AlarmReceiver
import com.thing.bangkit.soulmood.databinding.ActivityMainBinding
import com.thing.bangkit.soulmood.fragment.HomeFragment
import com.thing.bangkit.soulmood.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Log.d("TAGDATAKU", "onCreate: called")
        startPeriodicTask()

        binding.apply {
            handleFragment(HomeFragment.newInstance())
            fabChatbot.setOnClickListener {
                startActivity(Intent(this@MainActivity, ChatbotActivity::class.java))
            }

            ivBNProfile.setOnClickListener { setProfileFrament() }
            tvBNProfile.setOnClickListener { setProfileFrament() }
            ivBNHome.setOnClickListener { setHomeFragment() }
            tvBNHome.setOnClickListener { setHomeFragment() }

            fabChatbot.imageTintMode = null

        }

    }

    private fun handleFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction() //fragmentTransaction
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        fragmentTransaction.replace(R.id.forfragment, fragment).commit()
    }

    private fun ActivityMainBinding.setHomeFragment() {
        handleFragment(HomeFragment.newInstance())
        ivBNProfile.layoutParams.height = 80
        ivBNProfile.requestLayout()
        ivBNHome.layoutParams.height = 105
        ivBNHome.requestLayout()
        tvBNProfile.setTextColor(resources.getColor(R.color.grey, theme))
        ImageViewCompat.setImageTintList(
            ivBNProfile, ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.grey
                )
            )
        )
        tvBNHome.setTextColor(resources.getColor(R.color.white, theme))
        ImageViewCompat.setImageTintList(
            ivBNHome, ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.white
                )
            )
        )
    }

    private fun ActivityMainBinding.setProfileFrament() {
        handleFragment(ProfileFragment.newInstance())
        ivBNProfile.layoutParams.height = 105
        ivBNProfile.requestLayout()
        ivBNHome.layoutParams.height = 80
        ivBNHome.requestLayout()
        tvBNProfile.setTextColor(resources.getColor(R.color.white, theme))
        ImageViewCompat.setImageTintList(
            ivBNProfile, ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.white
                )
            )
        )
        tvBNHome.setTextColor(resources.getColor(R.color.grey, theme))
        ImageViewCompat.setImageTintList(
            ivBNHome, ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.grey
                )
            )
        )
    }


    private fun startPeriodicTask() {
        Log.d("TAGDATAKU", "startPeriodicTask: called")
        //set data
        AlarmReceiver().setRepeatingSendChatbotDataToApi(
            this,
            "ChatbotMessage"
        )


    }

}