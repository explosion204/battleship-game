package com.explosion204.battleship.viewmodels

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.explosion204.battleship.Constants.Companion.ERROR
import com.explosion204.battleship.Constants.Companion.FIRE_REQUEST_PASS
import com.explosion204.battleship.Constants.Companion.FIRE_RESPONSE_MISS
import com.explosion204.battleship.Constants.Companion.FIRE_RESPONSE_PASS
import com.explosion204.battleship.Constants.Companion.GAME_STATE_IN_LOBBY
import com.explosion204.battleship.Constants.Companion.GAME_STATE_IN_PROGRESS
import com.explosion204.battleship.Constants.Companion.GAME_STATE_LOADING
import com.explosion204.battleship.Constants.Companion.GAME_STATE_PAUSED
import com.explosion204.battleship.Constants.Companion.GUEST_DISCONNECTED
import com.explosion204.battleship.Constants.Companion.HOST_DISCONNECTED
import com.explosion204.battleship.GameController
import com.explosion204.battleship.MatrixGenerator
import com.explosion204.battleship.data.models.Session
import com.explosion204.battleship.data.repos.SessionRepository
import com.explosion204.battleship.data.repos.UserRepository
import com.explosion204.battleship.ui.util.CircleTransform
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception
import javax.inject.Inject

class GameViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    val gameController = GameController()

    var sessionId = MutableLiveData<Long?>(null)
    var hostId = MutableLiveData("")
    var guestId = MutableLiveData("")
    var hostReady = MutableLiveData(false)
    var guestReady = MutableLiveData(false)
    var gameRunning = MutableLiveData(false)
    var hostTurn = MutableLiveData(true)
    var hostBitmap = MutableLiveData<Bitmap>()
    var guestBitmap = MutableLiveData<Bitmap>()

    private var lockRequestUpdates = false
    private var lockResponseUpdates = true

    private var fireRequest = FIRE_REQUEST_PASS
    private var fireResponse = "0-0-$FIRE_RESPONSE_PASS"
    private var hostShips = 0
    private var guestShips = 0
    private var isHost = true
    private var picassoTargetHost: Target? = null
    private var picassoTargetGuest: Target? = null

    private var hostProfileImageLoaded = false
    private var guestProfileImageLoaded = false
    private var valueListener: ValueEventListener? = null


    // TODO: Delete session after the game finished
    // TODO: Second guest cannot connect to lobby (implemented, not tested)
    // TODO: Cannot connect to lobby with the same uid as host (implemented, not tested)
    // TODO: Check the following: session might be destroyed on guest force disconnect
    // Initialize new session if user is host (!!!onComplete callback executed only in GAME_STATE_LOADING!!!)
    fun initNewSession(userId: String, onComplete: () -> Unit) {
        sessionRepository.initNewSession(userId) {
            initLiveData(it) {
                if (hostProfileImageLoaded) {
                    if (gameController.gameState == GAME_STATE_LOADING) {
                        onComplete()
                        gameController.gameState = GAME_STATE_IN_LOBBY
                    }
                }
            }
        }
    }

    // Fetch data from existing session if user is guest
    fun fetchSession(
        sessionId: Long,
        userId: String,
        onComplete: () -> Unit,
        onFailure: () -> Unit
    ) {
        sessionRepository.findSession(sessionId,
            { ref ->
                isHost = false
                ref.child("guestId").setValue(userId).addOnSuccessListener {
                    initLiveData(ref) {
                        if (guestProfileImageLoaded && hostProfileImageLoaded) {
                            onComplete()
                        }
                    }
                }
            },
            {
                onFailure()
            })
    }

    // Subscribe lifecycle-aware fields of View Model to database changes
    private fun initLiveData(ref: DatabaseReference, onComplete: () -> Unit) {
        valueListener = ref.addValueEventListener(object : ValueEventListener {
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

                        if (!hostProfileImageLoaded) {
                            fetchHostProfileImageBitmap(session.hostId) {
                                onComplete()
                            }
                        }
                    }

                    if (guestId.value != session.guestId) {
                        guestId.postValue(session.guestId)

                        if (session.guestId != null && !guestProfileImageLoaded) {
                            fetchGuestProfileImageBitmap(session.guestId) {
                                onComplete()
                            }
                        }

                        if (session.guestId == GUEST_DISCONNECTED) {
                            guestProfileImageLoaded = false
                        }
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

                    hostTurn.postValue(session.hostTurn)

                    if (isHost) { // hostTurn = true
                        lockRequestUpdates = hostTurn.value!! // false
                        lockResponseUpdates = !hostTurn.value!! // true
                    } else { // hostTurn = true
                        lockRequestUpdates = !hostTurn.value!! // true
                        lockResponseUpdates = hostTurn.value!! // false
                    }

                    if (fireRequest != session.fireRequest && !lockRequestUpdates) {
                        fireRequest = session.fireRequest
                        val response = gameController.processFireRequest(session.fireRequest)
                        sendFireResponse(response)
                    }

                    if (fireResponse != session.fireResponse && !lockResponseUpdates) {
                        fireResponse = session.fireResponse
                        gameController.processFireResponse(session.fireResponse,
                            { // on hit

                            },
                            { // on miss
                                nextTurn()
                            })
                    }

                    if (hostShips != session.hostShips) {
                        hostShips = session.hostShips
                    }

                    if (guestShips != session.guestShips) {
                        guestShips = session.guestShips
                    }
                }
            }

        })

        ref.onDisconnect().removeValue()
    }

    private fun fetchHostProfileImageBitmap(hostId: String, onComplete: () -> Unit) {
        userRepository.getProfileImageUri(hostId) { profileImageUri ->
            picassoTargetHost = object : Target {
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    hostBitmap.postValue(bitmap)
                    hostProfileImageLoaded = true
                    onComplete()
                }
            }
            Picasso.get().load(profileImageUri).transform(CircleTransform())
                .into(picassoTargetHost!!)
        }
    }

    private fun fetchGuestProfileImageBitmap(guestId: String, onComplete: () -> Unit) {
        userRepository.getProfileImageUri(guestId) { profileImageUri ->
            picassoTargetGuest = object : Target {
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    guestBitmap.postValue(bitmap)
                    guestProfileImageLoaded = true
                    onComplete()
                }
            }
            Picasso.get().load(profileImageUri).transform(CircleTransform())
                .into(picassoTargetGuest!!)
        }
    }

    fun changeReady() {
        if (sessionId.value != null) {
            sessionRepository.updateSessionValue(
                sessionId.value!!,
                if (isHost) "hostReady" else "guestReady",
                if (isHost) !hostReady.value!! else !guestReady.value!!
            )
        }
    }

    fun setGameRunning(status: Boolean) {
        if (sessionId.value != null && hostReady.value!! && guestReady.value!!) {
            gameController.gameState = if (status) GAME_STATE_IN_PROGRESS else GAME_STATE_PAUSED
            sessionRepository.updateSessionValue(sessionId.value!!, "gameRunning", status)
        }
    }

    fun sendFireRequest(request: String) {
        if (sessionId.value != null) {
            if ((isHost && hostTurn.value!!) || (!isHost && !hostTurn.value!!)) {
                sessionRepository.updateSessionValue(sessionId.value!!, "fireRequest", request)
            }
        }
    }

    private fun sendFireResponse(response: String) {
        if (sessionId.value != null) {
            sessionRepository.updateSessionValue(sessionId.value!!, "fireResponse", response)
        }
    }

    private fun nextTurn() {
        if (sessionId.value != null) {
            sessionRepository.updateSessionValue(
                sessionId.value!!,
                "fireRequest",
                FIRE_REQUEST_PASS
            )
            sessionRepository.updateSessionValue(
                sessionId.value!!,
                "hostTurn",
                !hostTurn.value!!
            )
        }
    }

    override fun onCleared() {
        if (isHost) {
            sessionRepository.updateSessionValue(sessionId.value!!, "hostId", HOST_DISCONNECTED)
            sessionRepository.detachValueEventListener(sessionId.value!!, valueListener!!)
            sessionRepository.deleteSession(sessionId.value!!)
        } else {
            sessionRepository.updateSessionValue(sessionId.value!!, "guestId", GUEST_DISCONNECTED)
            sessionRepository.detachValueEventListener(sessionId.value!!, valueListener!!)
            sessionRepository.updateSessionValue(sessionId.value!!, "guestReady", false)
        }

        super.onCleared()
    }
}