package com.thing.bangkit.soulmood.splash

import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.model.SplashData

object SplashDataDummy {
    fun dataSplash(): ArrayList<SplashData>{
        val data=ArrayList<SplashData>()
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
        return data
    }
}