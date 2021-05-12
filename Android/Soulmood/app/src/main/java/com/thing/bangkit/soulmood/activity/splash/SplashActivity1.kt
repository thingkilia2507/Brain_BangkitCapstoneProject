package com.thing.bangkit.soulmood.activity.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.thing.bangkit.soulmood.activity.LoginActivity
import com.thing.bangkit.soulmood.activity.RegisterActivity
import com.thing.bangkit.soulmood.adapter.SplashViewAdapter
import com.thing.bangkit.soulmood.databinding.ActivitySplash1Binding

class SplashActivity1 : AppCompatActivity() {
    private var binding: ActivitySplash1Binding? = null

    private lateinit var splashViewAdapter: SplashViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplash1Binding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.apply {
            splashViewAdapter = SplashViewAdapter(SplashDataDummy.dataSplash(), splashViewPager)
            splashViewPager.adapter = splashViewAdapter
            splashViewPager.clipToPadding = false
            splashViewPager.clipChildren = false

            splashViewPager.offscreenPageLimit = 3
            splashViewPager.getChildAt(0).overScrollMode =
                RecyclerView.OVER_SCROLL_NEVER
            circleIndicator.animatePageSelected(2)
            splashViewAdapter.registerAdapterDataObserver(circleIndicator.adapterDataObserver)
            circleIndicator.setViewPager(splashViewPager)
            var compositePageTransformer =
                CompositePageTransformer()
            compositePageTransformer.addTransformer(MarginPageTransformer(40))
            compositePageTransformer.addTransformer { page, position ->
                val r: Float = 1 - Math.abs(position)
                page.scaleY = 0.85f + r * 0.15f
            }
            splashViewPager.setPageTransformer(compositePageTransformer)


            btnLogin.setOnClickListener {
                startActivity(Intent(this@SplashActivity1, LoginActivity::class.java))
                finish()
            }

            btnRegister.setOnClickListener {
                startActivity(Intent(this@SplashActivity1, RegisterActivity::class.java))
                finish()
            }


        }

    }



    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}