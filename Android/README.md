# Android

## 1. AI Chatbot Feature
This AI chatbot feature helps users to be able to tell stories about various things with the AI Chatbot
###### for API AI chatbot, we use url:
###### POST request
###### [API AI Chatbot](https://asia-southeast2-soulmood.cloudfunctions.net/chatbot)
###### example post parameter:
###### name = "joy"
###### message = "aku lagi sedig banget hari ini"

sample code that we use :
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
This feature is a chat forum feature where users can support and motivate each other. Users can make the name anonymous.
if there are hate speech sent by users. then the application will automatically hide the hate speech.
###### for API Detect Hate Speech, we use url:
###### POST request
###### [API Hate Speech](https://soulmood.uc.r.appspot.com)
###### example post parameter:
###### message = "dasar, bermuka dua"

sample code that we use :
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
The application can check the current mood of users through the user's message history with AI chatbot.



## 4. Dialy Motivation word
for dialy motication word, we use free public api
link API: 
GET request
[API Dialy motivation word](https://api.quotable.io/random)

## 5. Firebase
We use Firebase because Firebase has a realtime database feature that we can use to create realtime chat features.

### 1. Authentication
In your sign-in activity's onCreate method, get the shared instance of the FirebaseAuth object:
You can let your users authenticate with Firebase using their Email Accounts by integrating Email Sign-In into your app.
Example code for Login Authentication
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

### 2. Cloud Firestore
Store and sync your app data with this flexible, scalable NoSQL cloud-hosted database.
Cloud Firestore is a flexible, scalable database from Firebase and Google Cloud. It keeps your data in sync across client apps through realtime listeners and offers offline support so you can build responsive apps that work regardless of network latency or internet connectivity.

Example code firestore : 
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


