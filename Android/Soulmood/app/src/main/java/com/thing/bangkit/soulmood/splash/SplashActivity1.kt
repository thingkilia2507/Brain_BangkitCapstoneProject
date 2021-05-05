package com.thing.bangkit.soulmood.splash

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.adapter.SplashViewAdapter
import com.thing.bangkit.soulmood.databinding.ActivitySplash1Binding
import com.thing.bangkit.soulmood.model.SplashData

class SplashActivity1 : AppCompatActivity() {
    private var binding: ActivitySplash1Binding? = null
    private  var data=ArrayList<SplashData>()
    private lateinit var splashViewAdapter: SplashViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplash1Binding.inflate(layoutInflater)
        setContentView(binding?.root)
        data.add(
            SplashData(
                "Ceritakan Masalah Anda",
                "Beban terasa lebih ringan dengan menceritakan masalah anda.",
                R.drawable.splash_1
            )
        )
        data.add(
            SplashData(
                "Aplikasi SoulMood Siap Membantu Anda", "SoulMood menyediakan layanan \n" +
                        "Chatbot Cerdas yang bisa digunakan\n" +
                        "untuk bercerita.", R.drawable.splash_2
            )
        )
        data.add(
            SplashData(
                "Layanan dapat diakses\n" +
                        "secara gratis", "SoulMood menyediakan layanan \n" +
                        "Chatbot secara gratis.", R.drawable.splash_3
            )
        )
        binding?.apply {

            splashViewAdapter = SplashViewAdapter(data, splashViewPager)
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


        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}