package id.co.ingatin.data.network.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseConfig {
    val auth : FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore : FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
}