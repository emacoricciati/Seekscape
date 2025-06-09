package it.polito.mad.lab5g10.seekscape.firebase


import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.firestore


object Collections{
    private const val C_USERS = "Users"
    private const val C_TRAVELS = "Travels"
    private const val C_REQUESTS = "Requests"

    private val db: FirebaseFirestore by lazy {
        val instance = Firebase.firestore
        instance.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        instance
    }

    val requests by lazy{
        db.collection(C_REQUESTS)
    }

    val users by lazy {
        db.collection(C_USERS)
    }
    val travels by lazy {
        db.collection(C_TRAVELS)
    }

}
