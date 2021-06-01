package com.thing.bangkit.soulmood.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.thing.bangkit.soulmood.helper.DateHelper
import com.thing.bangkit.soulmood.helper.MyAsset
import com.thing.bangkit.soulmood.helper.SharedPref
import com.thing.bangkit.soulmood.model.MoodData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MoodTrackerViewModel : ViewModel() {

    private var moodData = MutableLiveData<ArrayList<MoodData>>()
    val date = DateHelper.getCurrentDateTime()


    fun insertMoodData(currentMood:String,moodCode:String,context: Context) {
        viewModelScope.launch(IO) {
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
            )
            insert1.collection("list_mood").add(
                mapOf(
                    "mood" to currentMood, "mood_code" to moodCode,
                    "updated_at" to date
                )
            )
        }
    }

    //untuk dashboard
    fun getDashboardMood(context: Context): LiveData<MoodData> {
        val date = DateHelper.getCurrentDateTime()
        val dataMood = MutableLiveData<MoodData>()
        val db = FirebaseFirestore.getInstance().collection("mood_tracker")
                .document("mood_tracker1")
                .collection(SharedPref.getPref(context, MyAsset.KEY_USER_ID).toString())
                .document(date.take(4))
                .collection(date.substring(5, 7))
                .orderBy("updated_at", Query.Direction.DESCENDING)
                .limit(1)
                db.addSnapshotListener { value, error ->
                    if (value != null) {
                        for (i in 0 until value.size()) {
                            val doc = value.documents[i]
                            dataMood.postValue(MoodData(doc.getString("current_mood").toString(),
                            doc.getString("mood_code").toString(),
                            doc.getString("updated_at").toString()))
                        }
                    }

                    //if mood documents empty
                    if(value?.documents?.size == 0){
                        dataMood.postValue(MoodData("","",""))
                    }
                }
        return dataMood
    }

    //untuk chart
    fun setMoodData(date: String, context: Context) {
        moodData.value?.clear()
        val data = ArrayList<MoodData>()
        viewModelScope.launch(IO) {
            val db = FirebaseFirestore.getInstance().collection("mood_tracker")
                .document("mood_tracker1")
                .collection(SharedPref.getPref(context, MyAsset.KEY_USER_ID).toString())
                .document(date.take(4))
                .collection(date.substring(5, 7))
            withContext(Dispatchers.Default) {
                db.get().addOnSuccessListener {value->
                    if (value != null) {
                        for (i in 0 until value.size()) {
                            Log.d("mydata", value.documents[i].toString())
                            val doc = value.documents[i]
                            data.add(
                                MoodData(
                                    doc.getString("current_mood").toString(),
                                    doc.getString("mood_code").toString(),
                                    doc.getString("updated_at").toString()
                                )
                            )
                        }
                        moodData.postValue(data)
                    }
                }




            }
        }
    }

    fun getMoodData(): LiveData<ArrayList<MoodData>> = moodData


}