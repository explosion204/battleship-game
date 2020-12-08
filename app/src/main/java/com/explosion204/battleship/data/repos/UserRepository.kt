package com.explosion204.battleship.data.repos

import android.graphics.Bitmap
import com.explosion204.battleship.data.models.User
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    fun getUser(id: String, callback: (ref: DocumentReference) -> Unit) {
        fireStore.collection("users")
            .whereEqualTo("userId", id)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null) {
                    val docs = it.result!!.documents

                    if (docs.size != 0) {
                        callback(docs[0].reference)
                    } else {
                        fireStore.collection("users")
                            .add(User(id, "Player"))
                            .addOnSuccessListener {
                                getUser(id, callback)
                            }
                    }
                }
            }
    }

    fun setUserNickname(id: String, newNickname: String) {
        fireStore.collection("users")
            .whereEqualTo("userId", id)
            .get()
            .addOnSuccessListener {
                it.documents[0].reference.update("nickname", newNickname)
            }
    }

    fun getProfileImageUri(id: String, callback: (uri: String) -> Unit) {
        fireStore.collection("users")
            .whereEqualTo("userId", id)
            .get()
            .addOnSuccessListener {
                if (it.documents.size != 0) {
                    callback(it.documents[0].get("profileImageUri").toString())
                }
            }
    }

    fun uploadProfileImage(uid: String, bitmap: Bitmap) {
        var stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

        val ref = storage.reference
            .child("profileImages")
            .child("$uid.jpeg")

        ref.putBytes(stream.toByteArray())
            .addOnSuccessListener {
                getDownloadUrl(uid, ref)
            }
    }

    private fun getDownloadUrl(uid: String, reference: StorageReference) {
        reference.downloadUrl
            .addOnSuccessListener { uri ->
                fireStore.collection("users")
                    .whereEqualTo("userId", uid)
                    .get()
                    .addOnSuccessListener { result ->
                        result.documents[0].reference.update("profileImageUri", uri.toString())
                    }
            }
    }
}