package com.explosion204.battleship.data.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.explosion204.battleship.Constants.Companion.FIRE_REQUEST_PASS
import com.explosion204.battleship.Constants.Companion.FIRE_RESPONSE_PASS
import com.explosion204.battleship.Constants.Companion.SESSIONS_DB
import com.explosion204.battleship.data.models.Session
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SessionRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    private val dbSessions = firebaseDatabase.getReference(SESSIONS_DB)

    fun initNewSession(hostId: String, onComplete: (ref: DatabaseReference) -> Unit) {
        dbSessions.child("sessionsCount").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                val newSession = Session(
                    id = snapshot.value as Long + 1,
                    hostId = hostId,
                    guestId = null,
                    hostReady = false,
                    guestReady = false,
                    gameRunning = false,
                    hostTurn = (0..1).random() == 1,
                    fireRequest = FIRE_REQUEST_PASS,
                    fireResponse = FIRE_RESPONSE_PASS,
                    hostShips = 10,
                    guestShips = 10)

                dbSessions.child("sessionsCount").setValue(newSession.id!!.toLong())

                dbSessions.child(newSession.id!!.toString())
                    .setValue(newSession)
                    .addOnSuccessListener {
                        onComplete(dbSessions.child(newSession.id!!.toString()))
                    }

            }
        })
    }

    fun updateSessionValue(sessionId: Long, field: String, value: Any) {
        dbSessions.child(sessionId.toString()).child(field).setValue(value)
    }

    fun deleteSession(sessionId: Long) {
        dbSessions.child(sessionId.toString()).ref.removeValue()
    }

    fun findSession(
        sessionId: Long,
        onSuccess: (ref: DatabaseReference) -> Unit,
        onFailure: () -> Unit
    ) {
        dbSessions.child(sessionId.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    onSuccess(snapshot.ref)
                }
                else {
                    onFailure()
                }
            }

        })
    }
}