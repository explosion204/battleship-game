package com.explosion204.battleship.data.repos

import com.explosion204.battleship.Constants.Companion.SESSIONS_DB
import com.explosion204.battleship.data.models.Session
import com.google.firebase.database.*
import javax.inject.Inject

class SessionRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    private val dbSessions = firebaseDatabase.getReference(SESSIONS_DB)

    fun initNewSession(hostId: String, onComplete: (ref: DatabaseReference) -> Unit) {
        dbSessions.child("sessionsCount")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}

                override fun onDataChange(snapshot: DataSnapshot) {
                    val newSession = Session(
                        id = snapshot.value as Long + 1,
                        hostId = hostId,
                        hostTurn = (0..1).random() == 1
                    )

                    dbSessions.child("sessionsCount").setValue(newSession.id!!.toLong())

                    dbSessions.child(newSession.id!!.toString())
                        .setValue(newSession)
                        .addOnSuccessListener {
                            onComplete(dbSessions.child(newSession.id!!.toString()))
                        }

                }
            })
    }

    // find session by its id
    fun findSession(
        sessionId: Long,
        onSuccess: (ref: DatabaseReference) -> Unit,
        onFailure: () -> Unit
    ) {
        dbSessions.child(sessionId.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists() && sessionId == snapshot.child("id").value) {
                        onSuccess(snapshot.ref)
                    } else {
                        onFailure()
                    }
                }

            })
    }

    fun updateSessionValue(sessionId: Long, field: String, value: Any, onComplete: (() -> Unit)?) {
        dbSessions.child(sessionId.toString()).child(field).setValue(value)

        if (onComplete != null) {
            onComplete()
        }
    }

    fun setValueOnDisconnect(ref: DatabaseReference, field: String, value: Any) {
        ref.child(field).onDisconnect().setValue(value)
    }

    fun deleteSession(sessionId: Long) {
        dbSessions.child(sessionId.toString()).ref.removeValue()
    }

    fun detachValueEventListener(sessionId: Long, listener: ValueEventListener) {
        dbSessions.child(sessionId.toString()).removeEventListener(listener)
    }
}