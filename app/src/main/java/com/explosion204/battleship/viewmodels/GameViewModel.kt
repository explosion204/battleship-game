package com.explosion204.battleship.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.explosion204.battleship.Constants.Companion.ERROR
import com.explosion204.battleship.data.models.Session
import com.explosion204.battleship.data.repos.SessionRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class GameViewModel @Inject constructor(private val sessionRepository: SessionRepository) : ViewModel() {
    var sessionId = MutableLiveData<Long?>(null)
    var hostId = MutableLiveData("")
    var guestId = MutableLiveData("")
    var hostReady = MutableLiveData(false)
    var guestReady = MutableLiveData(false)
    var gameRunning = MutableLiveData(false)
    var hostTurn = MutableLiveData(false)
    var fireRequest = MutableLiveData("")
    var fireResponse = MutableLiveData("")
    var hostShips = MutableLiveData(0)
    var guestShips = MutableLiveData(0)

    var lockRequestUpdates = false
    var lockResponseUpdates = false
    private var isHost = true

    // TODO: Delete session after the game finished
    fun initNewSession(hostId: String) {
        sessionRepository.initNewSession(hostId) {
            initMutableData(it)
        }
    }

    fun fetchSession(sessionId: Long, userId: String) {
        sessionRepository.findSession(sessionId,
        {
            isHost = false
            initMutableData(it)
            it.child("guestId").setValue(userId)
        },
        {
            //TODO: Implement failure callback
        })
    }

    private fun initMutableData(ref: DatabaseReference) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.e(ERROR, error.toString())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val session = snapshot.getValue(Session::class.java)

                if (session != null) {
                    if (sessionId.value != session.id) {
                        sessionId.postValue(session.id)
                    }

                    if (this@GameViewModel.hostId.value != session.hostId) {
                        this@GameViewModel.hostId.postValue(session.hostId)
                    }

                    if (guestId.value != session.guestId) {
                        guestId.postValue(session.guestId)
                    }

                    if (hostReady.value != session.hostReady) {
                        hostReady.postValue(session.hostReady)
                    }

                    if (guestReady.value != session.guestReady) {
                        guestReady.postValue(session.guestReady)
                    }

                    if (gameRunning.value != session.gameRunning) {
                        gameRunning.postValue(session.gameRunning)
                    }

                    if (hostTurn.value != session.hostTurn) {
                        hostTurn.postValue(session.hostTurn)
                    }

                    if (fireRequest.value != session.fireRequest && !lockRequestUpdates) {
                        fireRequest.postValue(session.fireRequest)
                    }

                    if (fireResponse.value != session.fireResponse && !lockResponseUpdates) {
                        fireResponse.postValue(session.fireResponse)
                    }

                    if (hostShips.value != session.hostShips) {
                        hostShips.postValue(session.hostShips)
                    }

                    if (guestShips.value != session.guestShips) {
                        guestShips.postValue(session.guestShips)
                    }
                }
            }

        })
    }

    fun changeReady() {
        if (sessionId.value != null) {
            sessionRepository.updateSessionValue(sessionId.value!!,
                if (isHost) "hostReady" else "guestReady",
                if (isHost) !hostReady.value!! else !guestReady.value!!)
        }
    }

    fun setGameRunning(status: Boolean) {
        if (sessionId.value != null && isHost && hostReady.value!! && guestReady.value!!) {
            sessionRepository.updateSessionValue(sessionId.value!!, "gameRunning", status)
        }
    }

    fun sendFireRequest(i: Int, j: Int) {
        if (sessionId.value != null) {
            sessionRepository.updateSessionValue(sessionId.value!!, "fireRequest", "$i-$j")
        }
    }

    fun finishTurn() {
        if (sessionId.value != null) {
            sessionRepository.updateSessionValue(sessionId.value!!, "gameRunning", !hostTurn.value!!)
        }
    }

    fun findSession(sessionId: Long, successCallback: () -> Unit, failureCallback: () -> Unit) {
        sessionRepository.findSession(sessionId, { successCallback() }, failureCallback)
    }
}