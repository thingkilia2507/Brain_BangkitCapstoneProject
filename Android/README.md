# Android

Our Application Preview Video : https://s.id/SoulmoodVideo
<p align="center">
  <a href="https://s.id/SoulmoodVideo">
    <img src="https://github.com/thingkilia2507/Brain_BangkitCapstoneProject/tree/main/ASSET/soulmood apps cover.png" alt="soulmood apps cover" width="200" height="200">
    <img src="https://github.com/thingkilia2507/Brain_BangkitCapstoneProject/tree/main/ASSET/mock-up-soulmood.png" alt="mock-up-soulmood" width="200" height="200">
  </a>

  <h3 align="center">SoulMood: Your Mental Health Care Application</h3>
</p>

## 1. AI Chatbot Feature
This AI chatbot feature helps users to be able to tell stories safely and comfortably with our smart bot provided by our machine learning team.
### For API AI chatbot, we use the url provided by our cloud team:
#### POST request
https://asia-southeast2-soulmood.cloudfunctions.net/chatbot
#### example post parameter:
 name = "joy",
 message = "aku lagi sedih banget hari ini"

###### sample code that we use :
* retrofit code is used to post data messages to the Chatbot API
```
private fun getInstance(URL: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
    @Volatile
    private var baseService: ApiService? = null

    @JvmStatic
    fun instanceService(): ApiService {
        if (baseService == null) {
            baseService = getInstance("https://asia-southeast2-soulmood.cloudfunctions.net/").create(ApiService::class.java)
        }
        return baseService as ApiService
    }
```
* Request chatbot response code
```
fun reqChatbotReply(context: Context){
        val name = SharedPref.getPref(context, MyAsset.KEY_NAME)!!
        if(messageReqReply.isNotEmpty()){
            val apiService = RetrofitBuild.instanceService()
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    val response = apiService.reqChatbotResponse(name, messageReqReply)
                    withContext(Dispatchers.Main){
                        if(response.code() == 200){
                            response.body()?.let {
                                recursionSendMessage(it.message, it.message.size-1, context)

                                _suggestionResponse.postValue(it.suggestion)
                                messageReqReply = ""
                            }
                        }else{
                            Toasty.error(context, "Maaf, Soulmood sedang dalam perbaikan.", Toasty.LENGTH_SHORT).show()
                        }
                    }
                }
                catch (e: Throwable){
                    withContext(Dispatchers.Main) {
                        Toasty.error(context, "Maaf, Soulmood sedang dalam perbaikan.", Toasty.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }
```



## 2. Human Chat Group Support
This feature is a feature that users can use to confide, share stories/experiences to strengthen each other so they can give "Human to human support". This feature is equipped with abusive speech detection so that if there is hate speech sent by the user, the application will automatically hide the hate speech.
### For API Detect Hate Speech,we use the url provided by our cloud team:
#### POST request
https://soulmood.uc.r.appspot.com
#### example post parameter:
 message = "dasar, bermuka dua"

###### sample code that we use :
This code is used to insert message data into the database, but before inserting new data, messages sent by the user will be checked into the hate speech API first to check for hate speech.

###### group_id = 1, message = "dasar, bermuka dua"
```
 fun insertNewChat(group_id: String, message: String, context: Context) {
        val service = RetrofitBuild.instanceBadwordService()
        viewModelScope.launch(Dispatchers.IO){
            try {
                val response = service.checkBadWordResponse(message)
                withContext(Dispatchers.Main){
                    if(response.code() == 200){
                        if(response.body() != null){
                            var aiMessage=""
                            var status="true"
                            if(!response.body()!!.status){
                                aiMessage = message
                                status = "false"
                            }else{
                                aiMessage = "*${response.body()!!.message}*"
                                status = "true"
                            }
                            val id = UUID.randomUUID().toString()
                            viewModelScope.launch(Dispatchers.IO) {
                                val database = db.collection("groups_chat")
                                    .document(group_id).collection("message").add(
                                        ChatMessage(id, SharedPref.getPref(context, MyAsset.KEY_NAME).toString(),
                                            SharedPref.getPref(context, MyAsset.KEY_EMAIL), message, aiMessage, DateHelper.getCurrentDateTime(), ""
                                        ,status)
                                    )
                                withContext(Dispatchers.Main){
                                    database.addOnSuccessListener {
                                    }.addOnFailureListener {
                                        Toasty.error(context, it.message.toString(), Toasty.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }else{
                        Toasty.error(context, "Maaf, Soulmood sedang dalam perbaikan.", Toasty.LENGTH_SHORT).show()
                    }
                }
            }catch (e:Throwable){
                withContext(Dispatchers.Main){
                    Toasty.error(context, "Maaf, Soulmood sedang dalam perbaikan.", Toasty.LENGTH_SHORT).show()
                }
            }

        }
    }
```

## 3. Mood Detection Feature
This application is equipped with a mood detection feature so that it can find out the user's mood from chat history with our chatbot, Soulmoo. With the user's mood history data, in the future, this data can be useful for consultation features with psychologists and psychiatrists

### For API Mood Detection, we use the url provided by our cloud team:
#### POST request
https://asia-southeast2-soulmood.cloudfunctions.net/moodDetector
#### example post parameter:
message = "<CB>:Hallo, aku bot soulmood<USER>:aku mau cerita nih, sekarang aku lagi sedih banget<CB>:Kamu boleh cerita kok tentang kesedihanmu"
##### Example result mood detection : "sedih"
###### sample code that we use :
        
```
        //first, we get messsage data from Database
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
        
        
        //end then, we send message data to Mood detection API to get mood result
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
```


## 4. Dialy Motivation word
This application will provide motivational word notification every morning.
For dialy motication word, we use free public api
link API: 
GET request
https://api.quotable.io/random

## 5. Firebase
In our application, we implement the Firebase platform which provides many tools and infrastructure from Google	to help us develop our applications, such as	Firebase Authentication and Cloud Firestore for our Realtime database.


### 5.1. Authentication
In your sign-in activity's onCreate method, get the shared instance of the FirebaseAuth object:
You can let your users authenticate with Firebase using their Email Accounts by integrating Email Sign-In into your app.

###### add dependency for firebase Auth in build.gradle
``` 
    implementation 'com.google.firebase:firebase-auth:21.0.0'
```

###### Example code for Login Authentication
 ```
private var auth = FirebaseAuth.getInstance()
private val db = FirebaseFirestore.getInstance()
val firebaseAuth = auth.signInWithEmailAndPassword(email, password)
                 firebaseAuth.addOnSuccessListener {
                    db.collection("users").whereEqualTo("email",email.toLowerCase())
                        .addSnapshotListener { value, _ ->
                            val data = value?.documents
                            if(data?.isNotEmpty() == true){
                                //get your data here
                                }
                            }
                        }
```

Example code for Register Authentication
```
 private var auth = FirebaseAuth.getInstance()
 private val db = FirebaseFirestore.getInstance()
   val firebaseAuth = auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                   //register is successful
                }.addOnFailureListener {
                   //register is failed
                }
            
```

### 5.2. Cloud Firestore
Store and sync your app data with this flexible, scalable NoSQL cloud-hosted database.
Cloud Firestore is a flexible, scalable database from Firebase and Google Cloud. It keeps your data in sync across client apps through realtime listeners and offers offline support so you can build responsive apps that work regardless of network latency or internet connectivity.

###### add dependency for Cloud Firestore build.gradle
``` 
    implementation 'com.google.firebase:firebase-firestore:23.0.0'
```

###### Example code firestore : 
```
FirebaseFirestore db = FirebaseFirestore.getInstance()
// Create a new user with a first and last name
Map<String, Object> user = new HashMap<>();
user.put("first", "Ada");
user.put("last", "Lovelace");
user.put("born", 1815);

// Add a new document with a generated ID
db.collection("users")
        .add(user)
        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });
```
 
# COMING SOON FEATURE

<p align="center">
    <img src="https://github.com/thingkilia2507/Brain_BangkitCapstoneProject/tree/main/ASSET/comingsoonkonsultasiSoulmood.png" alt="comingsoonkonsultasiSoulmood">
    <img src="https://github.com/thingkilia2507/Brain_BangkitCapstoneProject/tree/main/ASSET/comingsoonsoulcare.png" alt="comingsoonsoulcare">
  </p>
