package com.thing.bangkit.soulmood.activity.splash

import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.model.SplashData

object SplashDataDummy {
    fun dataSplash(): ArrayList<SplashData>{
        val data=ArrayList<SplashData>()
        data.add(
            SplashData(
                "Ceritakan Masalah Anda",
                "Beban terasa lebih ringan dengan saling berbagi cerita.",
                R.drawable.splash_1
            )
        )
        data.add(
            SplashData(
                "Aplikasi SoulMood Siap Membantu Anda", "SoulMood menyediakan layanan \n" +
                        "Chatbot Cerdas dengan menjaga\n" +
                        "informasi pribadi Anda.", R.drawable.splash_2
            )
        )
        data.add(
            SplashData(
                "Layanan Chatbot Gratis", "Chatbot SoulMood dapat diakses \n" +
                        "24 jam secara gratis.", R.drawable.splash_3
            )
        )
        return data
    }
}