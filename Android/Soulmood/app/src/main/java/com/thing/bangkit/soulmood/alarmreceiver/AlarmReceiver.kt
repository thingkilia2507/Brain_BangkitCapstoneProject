package com.thing.bangkit.soulmood.alarmreceiver

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.thing.bangkit.soulmood.BuildConfig
import com.thing.bangkit.soulmood.R
import com.thing.bangkit.soulmood.activity.MainActivity
import com.thing.bangkit.soulmood.helper.DateHelper
import com.thing.bangkit.soulmood.helper.DateHelper.currentDate
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.helper.MyAsset.CHATBOT_DB_NAME
import com.thing.bangkit.soulmood.helper.MyAsset.SOULMOOD_CHATBOT_NAME
import com.thing.bangkit.soulmood.helper.RetrofitBuild
import com.thing.bangkit.soulmood.helper.SharedPref
import com.thing.bangkit.soulmood.model.ChatbotMessage
import kotlinx.coroutines.*
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_MESSAGE_MOTIVATION_WORD = "motivation_word"
        const val EXTRA_MESSAGE_CHATBOT_DATA = "chatbot_data"
        private const val ID_REPEATING = 101
        private const val ID_REPEATING1 = 102
    }

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val message = intent.getStringExtra(EXTRA_MESSAGE_MOTIVATION_WORD)
        val message1 = intent.getStringExtra(EXTRA_MESSAGE_CHATBOT_DATA)
        if (message != null) {
            //get data from api and send to notif
            getQuoteOfTheDay(context)
        }
        Log.d("TAGDATAKU", "onReceive: message1 " + message1)
        if (message1 != null) {
            sendMessageToDb(context)
        }
    }

    fun getQuoteOfTheDay(context: Context){
        val service = RetrofitBuild.instanceQuotesService()
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = service.getDialyQuote()
                withContext(Dispatchers.Main) {
                    if (response.code() == 200) {
                        response.body()?.let {
                            var message = if (it.author.isNotEmpty()) {
                                "${it.content} \n- ${it.author} -"
                            } else {
                                it.content
                            }
                            showAlarmNotification(
                                context,
                                context.getString(R.string.dialy_motivation),
                                message,
                                ID_REPEATING
                            )
                        }
                    }else{
                        Log.v("retrofit error", response.code().toString())
                    }
                }
            }
            catch (e: Throwable){
                withContext(Dispatchers.Main) {
                    Log.v("retrofit error", e.message.toString())
                }
            }
        }
    }

    fun setRepeatingAlarmMotivationWord(context: Context, message: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(EXTRA_MESSAGE_MOTIVATION_WORD, message)

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

    fun setRepeatingSendChatbotDataToApi(context: Context, message: String) {
        sendMessageToDb(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(EXTRA_MESSAGE_CHATBOT_DATA, message)

        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING1, intent, 0)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            10800000, //repeat every 3 hours
            pendingIntent
        )
        Log.d("TAGDATAKU", "setRepeatingSendChatbotDataToApi: called " +message)

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


    private fun insertMoodData(currentMood: String, moodCode: String, context: Context) {
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
            Log.d("TAGDATAKU", "insertMoodData: berhasil")

        }.addOnFailureListener {
            Log.d("TAGDATAKU", "insertMoodData: gagal")

        }
        insert1.collection("list_mood").add(
            mapOf(
                "mood" to currentMood, "mood_code" to moodCode,
                "updated_at" to date
            )
        )
    }

    //background task insert mood data to db
    private fun sendMessageToDb(context: Context) {
        val message = StringBuilder("")

        //get dialy chatbot data from firestore
        GlobalScope.launch(Dispatchers.IO) {
            val chatbotData = async(Dispatchers.IO) {
                getChatbotData(context)
            }.await()
            delay(1000)
            Log.d("TAGDATAKU", "sendMessageToDb: " + chatbotData)
            if (chatbotData.isNotEmpty()) {
                for (i in 0 until chatbotData.size) {
                    if (chatbotData[i].name == SOULMOOD_CHATBOT_NAME) {
                        message.append("<CB>:${chatbotData[i].message}")
                    } else {
                        message.append("<USER>:${chatbotData[i].message}")
                    }
                }

                Log.d("TAGDATAKU", "sendMessageToDb message: \n" + message)

                val service = RetrofitBuild.instanceService()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = service.moodDetectorResponse(message.toString())
                        Log.d("TAGDATAKU", "sendMessageToDb: ${response.code()}" )
                        if (response.code() == 200) {
                                Log.d("TAGDATAKU", "sendMessageToDb: ${response.body() != null}" )
                            if (response.body() != null) {
                                val mood = response.body()!!.mood
                                var moodCode = ""
                                when (mood) {
                                    "Bahagia" -> moodCode = "4"
                                    "Sedih" -> moodCode = "3"
                                    "Takut" -> moodCode = "2"
                                    "Marah" -> moodCode = "1"
                                }
                                //insert data to firestore
                                insertMoodData(response.body()!!.mood, moodCode, context)
                            }
                        }

                    } catch (e: Throwable) {
                        Log.d("TAGDATAKU", "reqChatbotReply RetrofitFail: " + e.message.toString())
                    }
                }
            }
        }
    }

    private fun getChatbotData(context: Context): ArrayList<ChatbotMessage> {
        val db = FirebaseFirestore.getInstance()
        val chatData = ArrayList<ChatbotMessage>()

        CoroutineScope(Dispatchers.IO).launch {
            val data =
                db.collection(CHATBOT_DB_NAME).document(BuildConfig.VERSION_NAME)
                    .collection("user_chatbot")
                    .document(SharedPref.getPref(context, MyAsset.KEY_USER_ID).toString())
                    .collection("chatbot_messages")
                    .document(currentDate)
                    .collection("message")
                    .orderBy("created_at", Query.Direction.ASCENDING)

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
        return chatData
    }

}