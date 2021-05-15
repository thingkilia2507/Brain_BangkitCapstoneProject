package com.thing.bangkit.soulmood.helper

import java.text.SimpleDateFormat
import java.util.*


object DateHelper {
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }
    fun convertDateToMonthYearFormat(date:String):String{
        val df = SimpleDateFormat("yyyy-MM",Locale.ENGLISH)
        val df1 = SimpleDateFormat("MMM yyyy",Locale.ENGLISH)
        val date1 = df.parse(date)
        return df1.format(date1)
    }
}