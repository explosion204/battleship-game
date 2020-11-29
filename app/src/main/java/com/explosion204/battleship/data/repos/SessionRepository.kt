package com.explosion204.battleship.data.repos

import com.explosion204.battleship.Constants.Companion.FIRE_REQUEST_PASS
import com.explosion204.battleship.Constants.Companion.FIRE_RESPONSE_PASS
import com.explosion204.battleship.Constants.Companion.SESSIONS_DB
import com.explosion204.battleship.data.models.Session
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SessionRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    private val dbSessions = firebaseDatabase.getReference(SESSIONS_DB)

    suspend fun initNewSession(hostId: String): DatabaseReference {
        val newSession = Session(
            id = dbSessions.push().key,
            hostId = hostId,
            guestId = null,
            hostReady = false,
            guestReady = false,
            gameRunning = false,
            hostTurn = (0..1).random() == 1,
            fireRequest = FIRE_REQUEST_PASS,
            fireResponse = FIRE_RESPONSE_PASS,
            hostShips = 10,
            guestShips = 10
        )

        dbSessions.child(newSession.id!!)
            .setValue(newSession)
            .await()

        return dbSessions.child(newSession.id!!).ref
    }

    fun updateSessionValue(sessionId: String, field: String, value: Any) {
        dbSessions.child(sessionId).child(field).setValue(value)
    }
}