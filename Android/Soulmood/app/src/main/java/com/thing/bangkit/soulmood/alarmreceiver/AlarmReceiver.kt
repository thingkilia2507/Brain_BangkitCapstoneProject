package com.thing.bangkit.soulmood.alarmreceiver

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.activity.MainActivity
import com.thing.bangkit.soulmood.helper.DateHelper
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.helper.SharedPref
import com.thing.bangkit.soulmood.model.ChatbotMessage
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_MESSAGE1 = "message1"
        private const val ID_REPEATING = 101
        private const val ID_REPEATING1 = 102
    }

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val message = intent.getStringExtra(EXTRA_MESSAGE)
        val message1 = intent.getStringExtra(EXTRA_MESSAGE1)
        if (message != null) {
            showAlarmNotification(
                context,
                context.getString(R.string.dialy_motivation),
                message,
                ID_REPEATING
            )
        }
        if(message1 != null){
            sendMessageToDb(context)
        }
    }

    fun setRepeatingAlarm(context: Context, message: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, intent, 0)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun setRepeating1(context: Context, message: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(EXTRA_MESSAGE1, message)


        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING1, intent, 0)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            18000000, //repeat every 5 hours 18000000(5*60*60*1000)
            pendingIntent
        )
    }
    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, intent, 0)
        val pendingIntent1 = PendingIntent.getBroadcast(context, ID_REPEATING1, intent, 0)
        pendingIntent.cancel()
        pendingIntent1.cancel()
        alarmManager.cancel(pendingIntent)
        alarmManager.cancel(pendingIntent1)
    }

    private fun showAlarmNotification(
        context: Context,
        title: String,
        message: String,
        notifId: Int
    ) {
        val notifDetailIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(context)
            .addNextIntent(notifDetailIntent)
            .getPendingIntent(ID_REPEATING, PendingIntent.FLAG_UPDATE_CURRENT)

        val channelId = "Channel_Soulmood_1"
        val channelName = "Channel_Soulmood"
        val notificationManagerCompat =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val bigText = NotificationCompat.BigTextStyle()
        bigText.bigText(message)
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.soulmood_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(alarmSound)
            .setContentIntent(pendingIntent)
            .setStyle(bigText)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            builder.setChannelId(channelId)
            notificationManagerCompat.createNotificationChannel(channel)
        }
        val notification = builder.build()
        notificationManagerCompat.notify(notifId, notification)
    }



   private fun insertMoodData(currentMood:String,moodCode:String,context: Context) {
        val date = DateHelper.getCurrentDateTime()
        val insert1 = FirebaseFirestore.getInstance()
            .collection("mood_tracker")
            .document("mood_tracker1")
            .collection(SharedPref.getPref(context, MyAsset.KEY_USER_ID).toString())
            .document(date.take(4))
            .collection(date.substring(5, 7))
            .document(date.substring(8, 10))
        //untuk mood perhari(slalu update)
        insert1.set(
            mapOf(
                "current_mood" to currentMood, "mood_code" to moodCode,
                "updated_at" to date
            )
        ).addOnSuccessListener {
            println("berhasil")

        }.addOnFailureListener {
            println("gagal")

        }
        insert1.collection("list_mood").add(
            mapOf(
                "mood" to currentMood, "mood_code" to moodCode,
                "updated_at" to date
            )
        )
    }

    private fun sendMessageToDb(context: Context){
        var message= StringBuilder("")
        var it = getChatbotData(context)
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            if(it.isNotEmpty()){
                for(i in 0 until it.size){
                    if(it[i].name == "soulmood0280_chatbot"){
                        message.append("<CB>:${it[i].message}")
                    }else{
                        message.append("<USER>:${it[i].message}")
                    }
                }
            }else{
                message.append("")
            }
            showAlarmNotification(
                context,
                context.getString(R.string.dialy_motivation),
               "test background task is running",
                ID_REPEATING1
            )

            //retrofit send data to api and get the response
            insertMoodData("baik","4",context)

        }

    }

    private fun getChatbotData(context: Context):ArrayList<ChatbotMessage>{
         val db = FirebaseFirestore.getInstance()
        val chatData = ArrayList<ChatbotMessage>()
         val currentDate : String= SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

        CoroutineScope(Dispatchers.IO).launch {
            val data = db.collection("db_chatbot").document("1.0").collection("user_chatbot")
                .document(SharedPref.getPref(context, MyAsset.KEY_USER_ID).toString()).collection("chatbot_messages").document(currentDate).collection("message")
                .orderBy("created_at", Query.Direction.ASCENDING)
            withContext(Dispatchers.Main){
                data.addSnapshotListener { value, _ ->
                    value?.let {
                        for (message in value.documents) {
                            chatData.add(
                                ChatbotMessage(
                                    id = message.getString("id").toString(),
                                    name = message.getString("name").toString(),
                                    email = message.getString("email").toString(),
                                    message = message.getString("message").toString(),
                                    created_at = message.getString("created_at").toString()
                                )
                            )
                        }
                    }
                }
            }
        }
        return chatData
    }

}