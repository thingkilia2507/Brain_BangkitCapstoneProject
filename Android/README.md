# Android

## 1. AI Chatbot Feature
This AI chatbot feature helps users to be able to tell stories about various things with the AI Chatbot
for API AI chatbot, we use url:
POST request
[API AI Chatbot](https://asia-southeast2-soulmood.cloudfunctions.net/chatbot)
example post parameter:
name = "joy"
message = "aku lagi sedig banget hari ini"



## 2. Human Chat Group Support
This feature is a chat forum feature where users can support and motivate each other. Users can make the name anonymous.
if there are hate speech sent by users. then the application will automatically hide the hate speech.
###### for API Detect Hate Speech, we use url:
###### POST request
###### [API Hate Speech](https://soulmood.uc.r.appspot.com)
###### example post parameter:
###### message = "anjay loh, gak tau malu"







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


